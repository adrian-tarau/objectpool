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
     * @return a newly created object.
     */
    T makeObject() throws Exception;

    /**
     * Destroys an instance no longer needed by the pool.
     *
     * @param object the object to be destroyed
     */
    void destroyObject(T object) throws Exception;

    /**
     * Ensures that the instance is safe to be returned by the pool.
     *
     * @param object the pool object to be validated
     * @return <code>true</code> if the object is valid and can be reused, <code>false</code> otherwise
     */
    default boolean validateObject(T object) throws Exception {
        return true;
    }

}
