package net.microfalx.objectpool;

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
     * Increments a counter.
     *
     * @param name the name of the counter
     */
    public abstract void count(String name);

    /**
     * Increments a counter within a group.
     *
     * @param group the name of the group
     * @param name  the name of the counter
     */
    public abstract void count(String group, String name);

    static class NoMetrics extends Metrics {

        @Override
        public void count(String name) {
            // empty by design
        }

        @Override
        public void count(String group, String name) {
            // empty by design
        }
    }
}
