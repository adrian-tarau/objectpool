package net.microfalx.objectpool;

import net.microfalx.lang.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static net.microfalx.lang.ArgumentUtils.requireNonNull;
import static net.microfalx.lang.ExceptionUtils.rethrowException;
import static net.microfalx.lang.ExceptionUtils.rethrowExceptionAndReturn;
import static net.microfalx.lang.TimeUtils.THIRTY_SECONDS;
import static net.microfalx.lang.TimeUtils.millisSince;
import static net.microfalx.objectpool.ObjectPoolUtils.METRICS;

/**
 * An object pool implementation.
 *
 * @param <T> the type of the pooled object
 */
public class ObjectPoolImpl<T> implements ObjectPool<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ObjectPoolImpl.class);

    private static final long INITIAL_WAIT_TIME = 10;
    private static final long MAX_WAIT_TIME = 100;

    private final Options<T> options;
    private final BlockingDeque<PooledObjectImpl<T>> queue = new LinkedBlockingDeque<>();
    private final Collection<PooledObjectImpl<T>> objects = new CopyOnWriteArrayList<>();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Lock lock = new ReentrantLock();
    private final ObjectPoolMetricsImpl metrics = new ObjectPoolMetricsImpl();
    private volatile long lastAvailableUpdate = TimeUtils.oneHourAgo();
    private volatile boolean available = true;

    private static final Map<String, ObjectPoolImpl<?>> POOLS = new ConcurrentHashMap<>();

    static Collection<ObjectPool<?>> getPools() {
        return Collections.unmodifiableCollection(POOLS.values());
    }

    protected ObjectPoolImpl(Options<T> options) {
        requireNonNull(options);
        this.options = options;
        POOLS.put(options.getId(), this);
    }

    @Override
    public final String getId() {
        return options.getId();
    }

    @Override
    public final String getName() {
        return options.getName();
    }

    @Override
    public final String getDescription() {
        return options.getDescription();
    }

    @Override
    public final Options<T> getOptions() {
        return options;
    }

    @Override
    public final void addObject() {
        checkIfOpen();
        ADD_METRICS.time(getName(), (t) -> {
            lock.lock();
            try {
                if (objects.size() < options.getMaximum()) {
                    T object = options.getFactory().makeObject(this);
                    PooledObjectImpl<T> pooledObject = new PooledObjectImpl<>(this, object);
                    objects.add(pooledObject);
                    queue.offer(pooledObject);
                }
            } catch (Exception e) {
                rethrowException(getOptions().getFactory().createObjectCreationException(this, e));
            } finally {
                lock.unlock();
            }
        });
    }

    @Override
    public final T borrowObject() {
        checkIfOpen();
        long startTime = System.nanoTime();
        long endTime = startTime + options.getMaximumWait().toNanos();
        return BORROW_METRICS.time(getName(), () -> {
            long waitForAvailable = INITIAL_WAIT_TIME;
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
            return rethrowExceptionAndReturn(getOptions().getFactory().createObjectBorrowException(this, null));
        });
    }

    @Override
    public final void returnObject(T object) {
        requireNonNull(object);
        RETURN_METRICS.time(getName(), (t) -> {
            PooledObjectImpl<T> pooledObject = find(object);
            pooledObject.changeState(PooledObject.State.RETURNING);
            deactivate(pooledObject);
            pooledObject.changeState(PooledObject.State.IDLE);
            queue.offer(pooledObject);
        });
    }

    @Override
    public final void invalidateObject(T object) {
        requireNonNull(object);
        INVALIDATE_METRICS.time(getName(), (t) -> {
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
        });
    }

    @Override
    public final void clear() {
        CLEAR_METRICS.time(getName(), (t) -> {
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
        });
    }

    @Override
    public final void close() {
        if (closed.compareAndSet(false, true)) {
            doClose();
        }
    }

    @Override
    public final boolean isAvailable() {
        if (millisSince(lastAvailableUpdate) < THIRTY_SECONDS) {
            try {
                T object = borrowObject();
                returnObject(object);
                available = true;
            } catch (Exception e) {
                available = false;
            }
        }
        return available;
    }

    @Override
    public final boolean isClosed() {
        return closed.get();
    }

    @Override
    public final int getSize() {
        return objects.size();
    }

    @Override
    public final int getSize(PooledObject.State state) {
        requireNonNull(options);
        return (int) objects.stream().filter(p -> p.getState() == state).count();
    }

    @Override
    public final Collection<PooledObject<T>> getObjects() {
        return Collections.unmodifiableCollection(objects);
    }

    @Override
    public final Collection<PooledObject<T>> getObjects(PooledObject.State state) {
        requireNonNull(options);
        return objects.stream().filter(p -> p.getState() == state).collect(Collectors.toList());
    }

    @Override
    public final Metrics getMetrics() {
        return metrics;
    }

    private void doClose() {
        CLOSE_METRICS.count(getName());
        LOGGER.debug("Close object pool {}", getName());
        POOLS.remove(options.getId());
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
            LOGGER.debug("Destroy object {}", object);
            object.changeState(PooledObject.State.DESTROYING);
            try {
                options.getFactory().destroyObject(this, object.get());
            } catch (Exception e) {
                LOGGER.atDebug().setCause(e).log("Failed to destroy object {}", object);
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
            LOGGER.warn("Failed to deactivate object {}, destroy", object);
            destroyObject(object);
        }
    }

    private boolean activate(PooledObjectImpl<T> object) {
        if (!(options.getFactory() instanceof ActivableObjectFactory)) return true;
        try {
            ((ActivableObjectFactory<T>) options.getFactory()).activateObject(this, object);
            return true;
        } catch (Exception e) {
            LOGGER.warn("Failed to activate object {}, destroy", object);
            destroyObject(object);
            return false;
        }
    }

    private static final net.microfalx.metrics.Metrics ADD_METRICS = METRICS.withGroup("Add");
    private static final net.microfalx.metrics.Metrics BORROW_METRICS = METRICS.withGroup("Borrow");
    private static final net.microfalx.metrics.Metrics RETURN_METRICS = METRICS.withGroup("Return");
    private static final net.microfalx.metrics.Metrics INVALIDATE_METRICS = METRICS.withGroup("Invalidate");
    private static final net.microfalx.metrics.Metrics CLEAR_METRICS = METRICS.withGroup("Clear");
    private static final net.microfalx.metrics.Metrics CLOSE_METRICS = METRICS.withGroup("Close");
}
