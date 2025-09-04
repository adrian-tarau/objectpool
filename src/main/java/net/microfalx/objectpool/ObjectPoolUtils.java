package net.microfalx.objectpool;

import net.microfalx.lang.FormatterUtils;
import net.microfalx.lang.ObjectUtils;
import net.microfalx.metrics.Metrics;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.time.ZoneId.systemDefault;
import static net.microfalx.lang.ExceptionUtils.getRootCauseMessage;

public class ObjectPoolUtils {

    /**
     * The maximum allowed numbers of objects in any pool
     */
    public static final int MAXIMUM_POOL_SIZE = 1024 * 1024;

    /**
     * Holds all metrics related to object pool
     */
    protected static Metrics METRICS = Metrics.of("Object Pool");

    /**
     * Returns a zoned date/time if the instant seems to be set ( any value > 0).
     *
     * @param instant the instant
     * @return an optional with the date/time
     */
    public static ZonedDateTime fromInstant(long instant) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(instant), systemDefault());
    }

    /**
     * Returns a zoned date/time if the instant seems to be set ( any value > 0).
     *
     * @param instant the instant
     * @return an optional with the date/time
     */
    public static Optional<ZonedDateTime> optionalFromInstant(long instant) {
        return instant > 0 ? Optional.of(fromInstant(instant)) : Optional.empty();
    }

    /**
     * Creates the exception that will be thrown when object creation fails.
     *
     * @param pool      the pool which requested the object
     * @param type      the type of the object (it's a name, not a class)
     * @param throwable the exception thrown during object creation
     * @return a non-null instance
     */
    public static <T> Throwable createObjectCreationException(ObjectPool<T> pool, String type, Throwable throwable) {
        return new ObjectPoolException("Failed to create " + type + " in pool '" + ObjectUtils.getDescription(pool)
                + "', root cause: " + getRootCauseMessage(throwable), throwable);
    }

    /**
     * Creates the exception that will be thrown when an object cannot be borrowed.
     *
     * @param pool      the pool which requested the object
     * @param type      the type of the object (it's a name, not a class)
     * @param throwable the exception thrown during object creation, if any
     * @return a non-null instance
     */
    public static <T>  Throwable createObjectBorrowException(ObjectPool<T> pool, String type, Throwable throwable) {
        String timeOut = FormatterUtils.formatDuration(pool.getOptions().getMaximumWait());
        return new ObjectPoolException("Failed to borrow " + type + " from pool '" + ObjectUtils.getDescription(pool)
                + "' within the expected timeout (" + timeOut + ")", throwable);
    }
}
