package net.microfalx.objectpool;

import net.microfalx.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static net.microfalx.objectpool.ObjectPoolUtils.METRICS;
import static net.microfalx.objectpool.ObjectPoolUtils.requireNonNull;

/**
 * An object pool implementation.
 *
 * @param <T> the type of the pooled object
 */
final class ObjectPoolImpl<T> implements ObjectPool<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ObjectPoolImpl.class);

    private static final long INITIAL_WAIT_TIME = 10;
    private static final long MAX_WAIT_TIME = 100;

    private final OptionsImpl<T> options;
    private final BlockingDeque<PooledObjectImpl<T>> queue = new LinkedBlockingDeque<>();
    private final Collection<PooledObjectImpl<T>> objects = new CopyOnWriteArrayList<>();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Lock lock = new ReentrantLock();
    private final ObjectPoolMetricsImpl metrics = new ObjectPoolMetricsImpl();

    ObjectPoolImpl(OptionsImpl<T> options) {
        requireNonNull(options);
        this.options = options;
    }

    @Override
    public Options<T> getOptions() {
        return options;
    }

    @Override
    public void addObject() {
        checkIfOpen();

        try (Timer timer = METRICS.startTimer("add_object")) {
            lock.lock();
            try {
                if (objects.size() < options.getMaximum()) {
                    T object = options.getFactory().makeObject(this);
                    PooledObjectImpl<T> pooledObject = new PooledObjectImpl<>(this, object);
                    objects.add(pooledObject);
                    queue.offer(pooledObject);
                }
            } catch (Exception e) {
                throw new ObjectPoolException("Failed to create object with factory " + options.getFactory().getClass().getName(), e);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public T borrowObject() {
        checkIfOpen();

        long startTime = System.nanoTime();
        long endTime = startTime + options.getMaximumWait().toNanos();
        long waitForAvailable = INITIAL_WAIT_TIME;

        try (Timer timer = METRICS.startTimer("borrow_object")) {
            while (System.nanoTime() < endTime) {
                PooledObjectImpl<T> next = pollNext(waitForAvailable);
                if (next != null && next.getState() == PooledObject.State.IDLE) {
                    if (activate(next)) {
                        next.changeState(PooledObject.State.ACTIVE);
                        metrics.updateBorrowedDuration(System.nanoTime() - startTime);
                        return next.get();
                    }
                } else if (canAddMoreObjects()) {
                    addObject();
                }
                waitForAvailable = (long) Math.max(MAX_WAIT_TIME, waitForAvailable * 1.2f);
            }
            throw new ObjectPoolException("Timeout (" + options.getMaximumWait().getSeconds() + "s) waiting for an object");
        }
    }

    @Override
    public void returnObject(T object) {
        requireNonNull(object);

        try (Timer timer = METRICS.startTimer("return_object")) {
            PooledObjectImpl<T> pooledObject = find(object);
            pooledObject.changeState(PooledObject.State.RETURNING);
            deactivate(pooledObject);
            pooledObject.changeState(PooledObject.State.IDLE);
            queue.offer(pooledObject);
        }
    }

    @Override
    public void invalidateObject(T object) {
        requireNonNull(object);

        try (Timer timer = METRICS.startTimer("invalidate_object")) {
            PooledObjectImpl<T> pooledObject = find(object);
            pooledObject.changeState(PooledObject.State.DESTROYING);
            deactivate(pooledObject);
            pooledObject.changeState(PooledObject.State.DESTROYED);
            lock.lock();
            try {
                objects.remove(pooledObject);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void clear() {
        try (Timer timer = METRICS.startTimer("clear")) {
            lock.lock();
            try {
                objects.forEach(object -> {
                    if (object.getState() == PooledObject.State.IDLE) {
                        invalidateObject(object.get());
                    }
                });
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            doClose();
        }
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public int getSize() {
        return objects.size();
    }

    @Override
    public int getSize(PooledObject.State state) {
        requireNonNull(options);
        return (int) objects.stream().filter(p -> p.getState() == state).count();
    }

    @Override
    public Collection<PooledObject<T>> getObjects() {
        return Collections.unmodifiableCollection(objects);
    }

    @Override
    public Collection<PooledObject<T>> getObjects(PooledObject.State state) {
        requireNonNull(options);
        return objects.stream().filter(p -> p.getState() == state).collect(Collectors.toList());
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

    private void doClose() {
        LOGGER.debug("Close object pool");
        for (PooledObjectImpl<T> object : objects) {
            destroyObject(object);
        }
    }

    private boolean canAddMoreObjects() {
        return objects.size() < options.getMaximum();
    }

    private void checkIfOpen() {
        if (closed.get()) throw new ObjectPoolException("Object pool is closed");
    }

    private void destroyObject(PooledObjectImpl<T> object) {
        objects.remove(object);
        object.getLock().lock();
        try {
            LOGGER.debug("Destroy object " + object);
            object.changeState(PooledObject.State.DESTROYING);
            try {
                options.getFactory().destroyObject(this, object.get());
            } catch (Exception e) {
                LOGGER.debug("Failed to destroy object " + object, e);
            } finally {
                object.changeState(PooledObject.State.DESTROYED);
            }
        } finally {
            object.getLock().unlock();
        }
    }

    private PooledObjectImpl<T> find(T object) {
        for (PooledObjectImpl<T> pooledObject : objects) {
            if (pooledObject.get() == object) {
                return pooledObject;
            }
        }
        throw new ObjectPoolException("Returned object (" + object + ") was not created by this pool");
    }

    private PooledObjectImpl<T> pollNext(long waitForAvailable) {
        try {
            if (options.getStrategy() == Strategy.FIFO) {
                return queue.pollFirst(waitForAvailable, TimeUnit.MILLISECONDS);
            } else {
                return queue.pollLast(waitForAvailable, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            throw new ObjectPoolException("Failed to poll next object ", e);
        }
    }

    private void deactivate(PooledObjectImpl<T> object) {
        if (!(options.getFactory() instanceof ActivableObjectFactory)) return;
        try {
            ((ActivableObjectFactory<T>) options.getFactory()).deactivateObject(this, object);
        } catch (Exception e) {
            LOGGER.warn("Failed to deactivate object " + object + ", destroy");
            destroyObject(object);
        }
    }

    private boolean activate(PooledObjectImpl<T> object) {
        if (!(options.getFactory() instanceof ActivableObjectFactory)) return true;
        try {
            ((ActivableObjectFactory<T>) options.getFactory()).activateObject(this, object);
            return true;
        } catch (Exception e) {
            LOGGER.warn("Failed to activate object " + object + ", destroy");
            destroyObject(object);
            return false;
        }
    }
}
