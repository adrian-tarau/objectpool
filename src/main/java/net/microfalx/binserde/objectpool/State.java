package net.microfalx.binserde.objectpool;

/**
 * An enum which holdes the state of a pooled object.
 */
public enum State {

    /**
     * The object was borrowed and it is used.
     */
    ACTIVE,

    /**
     * The object sits idle in the pool
     */
    IDLE,

    /**
     * The object is abandoned and pending for invalidation and removal.
     */
    ABANDONED,

    /**
     * The object is in the process to be returned to the pool
     */
    RETURNING,

    /**
     * The object is in the queue, ready to be validated
     */
    VALIDATION,

    /**
     * The object is about to be destroyed
     */
    DESTROYING
}
