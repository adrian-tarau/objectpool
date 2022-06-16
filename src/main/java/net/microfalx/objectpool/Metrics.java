package net.microfalx.objectpool;

import java.util.function.Supplier;

/**
 * An abstraction to track various metrics.
 */
public abstract class Metrics {

    private static volatile Metrics instance;

    /**
     * A prefix added to all meters.
     */
    protected static final String GROUP_PREFIX = "Object Pool";

    /**
     * A default group for metrics.
     */
    public static final String DEFAULT_GROUP = "Misc";

    /**
     * Returns an instance of metrics.
     *
     * @return a non-null instance
     */
    public static Metrics get() {
        if (instance == null) {
            synchronized (Metrics.class) {
                if (instance == null) {
                    try {
                        instance = ObjectPoolUtils.createInstance("net.microfalx.objectpool.MicroMeterMetrics");
                    } catch (Exception e) {
                        instance = new NoMetrics();
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Increments a counter within a group.
     *
     * @param group the name of the group
     * @param name  the name of the counter
     */
    public static long count(String group, String name) {
        return get().doCount(group, name);
    }

    /**
     * Increments a counter within a group.
     *
     * @param group the name of the group
     * @param name  the name of the counter
     */
    public abstract long doCount(String group, String name);

    /**
     * Times a block of code.
     *
     * @param group the name of the group
     * @param name  the name of the timer
     */
    public static <T> T time(String group, String name, Supplier<T> supplier) {
        return get().doTime(group, name, supplier);
    }

    /**
     * Times a block of code.
     *
     * @param group the name of the group
     * @param name  the name of the timer
     */
    public abstract <T> T doTime(String group, String name, Supplier<T> supplier);

    static class NoMetrics extends Metrics {

        @Override
        public long doCount(String group, String name) {
            return 0;
        }

        @Override
        public <T> T doTime(String group, String name, Supplier<T> supplier) {
            return supplier.get();
        }
    }
}
