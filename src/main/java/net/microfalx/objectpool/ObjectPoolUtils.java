package net.microfalx.objectpool;

import net.microfalx.metrics.Metrics;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.time.ZoneId.systemDefault;

public class ObjectPoolUtils {

    /**
     * The maximum allowed numbers of objects in any pool
     */
    public static final int MAXIMUM_POOL_SIZE = 1024 * 1024;

    /**
     * Holds all metrics related to object pool
     */
    protected static Metrics METRICS = Metrics.of("object_pool");

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
}
