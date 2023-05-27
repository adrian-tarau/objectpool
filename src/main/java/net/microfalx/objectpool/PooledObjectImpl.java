package net.microfalx.objectpool;

import java.util.concurrent.locks.ReentrantLock;

import static net.microfalx.lang.ArgumentUtils.requireNonNull;


/**
 * Implementation of {@link  PooledObject}.
 *
 * @param <T> the type of pooled objects
 */
final class PooledObjectImpl<T> implements PooledObject<T> {

    private final ObjectPool<T> owner;
    private final T object;
    private final PooledObjectMetricsImpl metrics;
    private final ReentrantLock lock = new ReentrantLock();

    private volatile State state = State.IDLE;

    PooledObjectImpl(ObjectPool<T> owner, T object) {
        requireNonNull(owner);
        requireNonNull(object);
        this.owner = owner;
        this.object = object;
        this.metrics = new PooledObjectMetricsImpl();
    }

    @Override
    public ObjectPool<T> getOwner() {
        return owner;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public T get() {
        return object;
    }

    @Override
    public PooledObject.Metrics getMetrics() {
        return metrics;
    }

    void changeState(State state) {
        this.state = state;
    }

    ReentrantLock getLock() {
        return lock;
    }
}
