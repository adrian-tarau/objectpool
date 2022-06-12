package net.microfalx.binserde.objectpool;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import static net.microfalx.binserde.objectpool.ObjectPoolUtils.requireNonNull;

/**
 * An object pool implementation
 *
 * @param <T> the type of the pooled object
 */
class ObjectPoolImpl<T> implements ObjectPool<T> {

    private final OptionsImpl<T> options;
    private final Deque<PooledObjectImpl<T>> queue = new LinkedBlockingDeque<>();

    ObjectPoolImpl(OptionsImpl<T> options) {
        requireNonNull(options);
        this.options = options;
    }

    @Override
    public Options getOptions() {
        return null;
    }

    @Override
    public void addObject() {

    }

    @Override
    public T borrowObject() {
        return null;
    }

    @Override
    public void returnObject(T object) {

    }

    @Override
    public void invalidateObject(T object) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void close() {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getSize(State state) {
        return 0;
    }

    @Override
    public Collection<PooledObject<T>> getObjects(State state) {
        return null;
    }
}
