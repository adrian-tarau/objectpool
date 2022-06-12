package net.microfalx.binserde.objectpool;

/**
 * An object pool which allows objects to be <code>activated</code> before they can be used.
 * <p>
 * This type of factory is useful for those objects which cannot be preserved in the pool without
 * making them "not in use". This requires the object to be made "in use" before it can be borrowed.
 *
 * @param <T> the type of pooled objects
 */
public interface ActivableObjectFactory<T> extends ObjectFactory<T> {

    /**
     * Activates an instance before it is used (borrowed).
     *
     * @param object the pooled object
     */
    void activateObject(PooledObject<T> object) throws Exception;

    /**
     * Deactivates an instance returned to the pool, before it is switched to an idle state and returned to the pool.
     *
     * @param object the pooled object
     */
    void deactivateObject(PooledObject<T> object) throws Exception;
}
