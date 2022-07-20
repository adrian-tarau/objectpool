package net.microfalx.objectpool;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Duration.ofNanos;
import static net.microfalx.objectpool.ObjectPoolUtils.fromInstant;

public class ObjectPoolMetricsImpl implements ObjectPool.Metrics {

    private final long created = System.currentTimeMillis();

    private final AtomicLong borrowedCounter = new AtomicLong();
    private final AtomicLong borrowedDuration = new AtomicLong();

    private final AtomicLong releasedCounter = new AtomicLong();
    private final AtomicLong releasedDuration = new AtomicLong();

    @Override
    public ZonedDateTime getCreatedTime() {
        return fromInstant(created);
    }

    @Override
    public long getReleasedCount() {
        return releasedCounter.get();
    }

    @Override
    public Duration getReleasedDuration() {
        return ofNanos(releasedDuration.get());
    }

    @Override
    public long getBorrowedCount() {
        return borrowedCounter.get();
    }

    @Override
    public Duration getBorrowedDuration() {
        return ofNanos(borrowedDuration.get());
    }

    void updateBorrowedDuration(long duration) {
        borrowedDuration.addAndGet(duration);
        borrowedCounter.incrementAndGet();
    }

    void updateReleaseCount() {
        releasedCounter.incrementAndGet();
    }

    void updateReleaseDuration(long duration) {
        releasedDuration.addAndGet(duration);
    }
}
