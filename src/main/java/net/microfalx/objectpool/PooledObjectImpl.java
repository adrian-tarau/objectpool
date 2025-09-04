package net.microfalx.objectpool;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static net.microfalx.lang.ArgumentUtils.requireNonNull;


/**
 * Implementation of {@link  PooledObject}.
 *
 * @param <T> the type of pooled objects
 */
final class PooledObjectImpl<T> implements PooledObject<T> {

    private final String id = UUID.randomUUID().toString();
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
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return object.toString();
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PooledObjectImpl<?> that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    ReentrantLock getLock() {
        return lock;
    }
}
