package net.microfalx.objectpool;

import io.micrometer.core.instrument.MeterRegistry;

import static net.microfalx.objectpool.ObjectPoolUtils.requireNonNull;

public class MicroMeterMetrics extends Metrics {

    private final MeterRegistry registry = io.micrometer.core.instrument.Metrics.globalRegistry;

    private static final String DEFAULT_GROUP = "Misc";
    private final static String[] TAGS = {"object", "pool"};

    @Override
    public void count(String name) {
        count(DEFAULT_GROUP, name);
    }

    @Override
    public void count(String group, String name) {
        requireNonNull(name);
        group = finalGroupName(group);
        registry.counter(finalName(group, name), TAGS);
    }

    private static String finalGroupName(String group) {
        if (group == null) group = DEFAULT_GROUP;
        return finalName(GROUP_PREFIX, group);
    }

    private static String finalName(String group, String name) {
        return normalize(group) + "." + normalize(name);
    }

    private static String normalize(String name) {
        if (name == null) return "na";
        name = name.toLowerCase();
        name = name.replace(' ', '_');
        return name;
    }
}
