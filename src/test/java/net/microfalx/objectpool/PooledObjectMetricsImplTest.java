package net.microfalx.objectpool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PooledObjectMetricsImplTest {

    private static final PooledObjectMetricsImpl metrics = new PooledObjectMetricsImpl();

    @Test
    void borrowObject() {
        assertEquals(0, metrics.getBorrowedCount());
        assertEquals(0, metrics.getBorrowedDuration().toNanos());
        assertTrue(metrics.getLastBorrowedTime().isEmpty());

        metrics.updateBorrowCount();
        assertEquals(1, metrics.getBorrowedCount());
        assertTrue(metrics.getLastBorrowedTime().isPresent());

        metrics.updateBorrowedDuration(100);
        assertEquals(100, metrics.getBorrowedDuration().toNanos());
    }

    @Test
    void lastUsed() {
        assertTrue(metrics.getLastUsedTime().isEmpty());
        metrics.updateLastUsed(System.currentTimeMillis());
        assertTrue(metrics.getLastUsedTime().isPresent());
    }

}