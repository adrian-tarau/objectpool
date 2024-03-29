package net.microfalx.objectpool;

/**
 * An interface which controls the life-cycle of pooled objects.
 *
 * @param <T> the type of pooled objects
 */
public interface ObjectFactory<T> {

    /**
     * Creates an instance that can be served by the pool and wrap it in a PooledObject to be managed by the pool.
     *
     * @param pool the pool which requested the object
     * @return a newly created object.
     */
    T makeObject(ObjectPool<T> pool) throws Exception;

    /**
     * Destroys an instance no longer needed by the pool.
     *
     * @param pool   the pool which requested the object to be destroyed
     * @param object the object to be destroyed
     */
    void destroyObject(ObjectPool<T> pool, T object) throws Exception;

    /**
     * Ensures that the instance is safe to be returned by the pool.
     *
     * @param pool   the pool which requested the object validation
     * @param object the pool object to be validated
     * @return <code>true</code> if the object is valid and can be reused, <code>false</code> otherwise
     */
    default boolean validateObject(ObjectPool<T> pool, T object) throws Exception {
        return true;
    }

}
