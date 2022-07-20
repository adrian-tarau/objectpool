package net.microfalx.objectpool;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Holds information about a pooled object.
 *
 * @param <T> the type of the pooled object
 */
public interface PooledObject<T> {

    /**
     * Returns the pool which owns this pooled object.
     *
     * @return a non-null instance
     */
    ObjectPool<T> getOwner();

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

    /**
     * Returns metrics about this pooled object.
     *
     * @return a non-null instance
     */
    Metrics getMetrics();

    /**
     * An enum which holdes the state of a pooled object.
     */
    enum State {

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
        DESTROYING,

        /**
         * The object was destroyed.
         */
        DESTROYED,
    }

    /**
     * An interface which provides metrics about pooled objects.
     */
    interface Metrics {

        /**
         * Returns the time when the object was created and added to the pool.
         *
         * @return a non-null instance
         */
        ZonedDateTime getCreatedTime();

        /**
         * Returns the time when an object was last borrowed from the pool.
         *
         * @return when was last time borrowed
         */
        Optional<ZonedDateTime> getLastBorrowedTime();

        /**
         * Returns the time when an object was last time returned to the pool.
         *
         * @return a non-null instance if an object was returned, null otherwise
         */
        Optional<ZonedDateTime> getLastReturnedTime();

        /**
         * Returns the time when an object was used last time.
         * <p>
         * The last used can only be reported if the object implements {@link UsageReporter}.
         *
         * @return the last used time, null if it cannot be provided
         */
        Optional<ZonedDateTime> getLastUsedTime();

        /**
         * Returns the number of times this object has been borrowed.
         *
         * @return a positive integer
         */
        long getBorrowedCount();

        /**
         * Returns the amount of time this object spent in the active state (borrowed).
         *
         * @return the duration
         */
        Duration getBorrowedDuration();
    }
}
