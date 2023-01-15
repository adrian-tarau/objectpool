package net.microfalx.objectpool;

import net.microfalx.metrics.Metrics;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
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
     * Checks that the specified object reference is not {@code null}.
     *
     * @param value the object reference to check for nullity
     * @param <T>   the type of the reference
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T requireNonNull(T value) {
        if (value == null) throw new IllegalArgumentException("Argument cannot be NULL");
        return value;
    }

    /**
     * Checks that the specified integer is within bounds.
     *
     * @param value   the value to check
     * @param minimum the minimum expected value, inclusive
     * @param maximum the maximum expected value, inclusive
     * @return the value
     */
    public static int requireBounded(int value, int minimum, int maximum) {
        if (value < minimum) throw new IllegalArgumentException("A minimum value of " + minimum + " is expected");
        if (value > maximum) throw new IllegalArgumentException("A maximum value of " + maximum + " is expected");
        return value;
    }

    /**
     * Returns whether the string is empty.
     *
     * @param value the string to validate
     * @return {@code true} if empty, @{code false} otherwise
     */
    public static boolean isEmpty(CharSequence value) {
        return value == null || value.length() == 0;
    }

    /**
     * Returns if the object is "empty": a null object, an empty string({@link CharSequence}) or an empty collection.
     * Any other object type returns false(object not "empty")
     *
     * @param object an object instance
     * @return true if object is considered "empty"(does not carry out information)
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof CharSequence) {
            return isEmpty((CharSequence) object);
        } else if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        } else if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else {
            return false;
        }
    }

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
     * Returns whether the string is not empty.
     *
     * @param value the string to validate
     * @return {@code true} if not empty, @{code false} otherwise
     */
    public static boolean isNotEmpty(CharSequence value) {
        return !isEmpty(value);
    }

    public static String defaultIfEmpty(String value, String defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

    public static String defaultIfNull(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }
}
