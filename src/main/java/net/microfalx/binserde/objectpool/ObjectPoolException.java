package net.microfalx.binserde.objectpool;

/**
 * An exception for object pool failues.
 */
public class ObjectPoolException extends RuntimeException {

    public ObjectPoolException(String message) {
        super(message);
    }

    public ObjectPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}
