package net.microfalx.binserde.objectpool;

/**
 * Holds information about a pooled object.
 *
 * @param <T> the type of the pooled object
 */
public interface PooledObject<T> {

    /**
     * Returns an identifier for this pooled object.
     *
     * @return a non-null instance
     */
    Object getId();

    /**
     * Returns the state of the object.
     *
     * @return a non-null instance
     */
    State getState();

    /**
     * Returns the underlying object that is wrapped by this instance.
     *
     * @return a non-null instance
     */
    T get();
}
