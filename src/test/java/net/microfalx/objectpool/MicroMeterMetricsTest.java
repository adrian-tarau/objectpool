package net.microfalx.objectpool;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MicroMeterMetricsTest {

    private MicroMeterMetrics metrics = new MicroMeterMetrics();

    @BeforeAll
    static void registerRegistry() {
        Metrics.addRegistry(new SimpleMeterRegistry());
    }

    @Test
    void count() {
        metrics.count("c1");
        metrics.count("g1", "c1");
        assertEquals(1, Metrics.counter("object_pool.misc.c1").count());
        assertEquals(1, Metrics.counter("object_pool.g1.c1").count());
    }

}