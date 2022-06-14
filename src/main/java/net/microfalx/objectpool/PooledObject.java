package net.microfalx.objectpool;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Holds information about a pooled object.
 *
 * @param <T> the type of the pooled object
 */
public interface PooledObject<T> {

    /**
     * Returns the state of the object.
     *
     * @return a non-null instance
     */
    State getState();

    /**
     * Returns the time when the object was created and added to the pool.
     *
     * @return a non-null instance
     */
    ZonedDateTime getCreatedTime();

    /**
     * Returns the time when an object was last borrowed from the pool.
     *
     * @return a non-null instance if an object was borrowed, null otherwise
     */
    ZonedDateTime getLastBorrowedTime();

    /**
     * Returns the time when an object was last time returned to the pool.
     *
     * @return a non-null instance if an object was returned, null otherwise
     */
    ZonedDateTime getLastReturnedTime();

    /**
     * Returns the time when an object was used last time.
     * <p>
     * The last used can only be reported if the object implements {@link  UsageReporter}.
     *
     * @return the last used time, null if it cannot be provided
     */
    ZonedDateTime getLastUsedTime();

    /**
     * Returns the number of times this object has been borrowed.
     *
     * @return a positive integer
     */
    long getBorrowedCount();

    /**
     * Returns the amount of time this object last spent in the active state (borrowed).
     *
     * @return the duration
     */
    Duration getBorrowedDuration();

    /**
     * Returns the amount of time this object spent in the active state (borrowed).
     *
     * @return the duration
     */
    Duration getTotalBorrowedDuration();

    /**
     * Returns the amount of time that this object last spend in the idle state.
     *
     * @return the duration
     */
    Duration getIdleDuration();

    /**
     * Returns the amount of time that this object last spend in the idle state.
     *
     * @return the duration
     */
    Duration getTotalIdleDuration();

    /**
     * Returns the underlying object that is wrapped by this instance.
     *
     * @return a non-null instance
     */
    T get();
}
