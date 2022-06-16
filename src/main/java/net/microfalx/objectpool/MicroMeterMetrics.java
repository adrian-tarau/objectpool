package net.microfalx.objectpool;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.function.Supplier;

import static net.microfalx.objectpool.ObjectPoolUtils.requireNonNull;

public class MicroMeterMetrics extends Metrics {

    private final MeterRegistry registry = io.micrometer.core.instrument.Metrics.globalRegistry;

    private final static String[] TAGS = {"object", "pool"};

    @Override
    public long doCount(String group, String name) {
        requireNonNull(group);
        requireNonNull(name);
        group = finalGroupName(group);
        Counter counter = registry.counter(finalName(group, name), TAGS);
        counter.increment();
        return (long) counter.count();
    }

    @Override
    public <T> T doTime(String group, String name, Supplier<T> supplier) {
        requireNonNull(group);
        requireNonNull(name);
        group = finalGroupName(group);
        Timer timer = registry.timer(finalName(group, name), TAGS);
        return timer.record(supplier);
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
