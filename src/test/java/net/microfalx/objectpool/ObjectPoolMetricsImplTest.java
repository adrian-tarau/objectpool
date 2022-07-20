package net.microfalx.objectpool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectPoolMetricsImplTest {

    private static final ObjectPoolMetricsImpl metrics = new ObjectPoolMetricsImpl();

    @Test
    void borrowObject() {
        assertEquals(0, metrics.getBorrowedCount());
        assertEquals(0, metrics.getBorrowedDuration().toNanos());

        metrics.updateBorrowedDuration(100);
        assertEquals(1, metrics.getBorrowedCount());
        assertEquals(100, metrics.getBorrowedDuration().toNanos());
    }

    @Test
    void releaseObject() {
        assertEquals(0, metrics.getReleasedCount());
        assertEquals(0, metrics.getReleasedDuration().toNanos());

        metrics.updateReleaseCount();
        assertEquals(1, metrics.getReleasedCount());

        metrics.updateReleaseDuration(100);
        assertEquals(100, metrics.getReleasedDuration().toNanos());
    }

}