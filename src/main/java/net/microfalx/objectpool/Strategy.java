package net.microfalx.objectpool;

/**
 * Holds a strategy used to return objects from pool.
 */
public enum Strategy {

    /**
     * Last object added to the pool is borrowed first.
     */
    LIFO,

    /**
     * First object added to the pool is borrowed first.
     */
    FIFOE
}
