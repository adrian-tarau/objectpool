package net.microfalx.objectpool;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetricsTest {

    @BeforeAll
    static void registerRegistry() {
        io.micrometer.core.instrument.Metrics.globalRegistry.clear();
        io.micrometer.core.instrument.Metrics.addRegistry(new SimpleMeterRegistry());
    }

    @Test
    void get() {
        assertNotNull(Metrics.get());
    }

    @Test
    void count() {
        assertEquals(1, Metrics.count("a", "a"));
    }

    @Test
    void time() {
        assertEquals(1, Metrics.time("a", "t1", () -> 1));
    }


}