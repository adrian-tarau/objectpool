package net.microfalx.objectpool;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicroMeterMetricsTest {

    private MicroMeterMetrics metrics = new MicroMeterMetrics();

    @BeforeAll
    static void registerRegistry() {
        io.micrometer.core.instrument.Metrics.globalRegistry.clear();
        Metrics.addRegistry(new SimpleMeterRegistry());
    }

    @Test
    void count() {
        metrics.count("g1", "c1");
        assertEquals(1, (long) Metrics.counter("object_pool.g1.c1").count());
    }

    @Test
    void time() {
        metrics.time("g1", "t1", () -> 1);
        assertEquals(1, Metrics.timer("object_pool.g1.t1").count());
        assertEquals(100, Metrics.timer("object_pool.g1.t1").totalTime(TimeUnit.MILLISECONDS));
    }

}