package net.microfalx.objectpool;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Duration.ofNanos;
import static net.microfalx.objectpool.ObjectPoolUtils.fromInstant;
import static net.microfalx.objectpool.ObjectPoolUtils.optionalFromInstant;

public class PooledObjectMetricsImpl implements PooledObject.Metrics {

    private final long created = System.currentTimeMillis();

    private volatile long lastBorrowed;
    private volatile long lastReturned;
    private volatile long lastUsed;

    private final AtomicLong borrowedCounter = new AtomicLong();
    private final AtomicLong borrowedDuration = new AtomicLong();

    @Override
    public ZonedDateTime getCreatedTime() {
        return fromInstant(created);
    }

    @Override
    public Optional<ZonedDateTime> getLastBorrowedTime() {
        return optionalFromInstant(lastBorrowed);
    }

    @Override
    public Optional<ZonedDateTime> getLastReturnedTime() {
        return optionalFromInstant(lastReturned);
    }

    @Override
    public Optional<ZonedDateTime> getLastUsedTime() {
        return optionalFromInstant(lastUsed);
    }

    @Override
    public long getBorrowedCount() {
        return borrowedCounter.get();
    }

    @Override
    public Duration getBorrowedDuration() {
        return ofNanos(borrowedDuration.get());
    }

    void updateBorrowCount() {
        lastBorrowed = System.currentTimeMillis();
        borrowedCounter.incrementAndGet();
    }

    void updateBorrowedDuration(long duration) {
        borrowedDuration.addAndGet(duration);
    }

    void updateLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    void updateLastReturned(long lastReturned) {
        this.lastReturned = lastReturned;
    }
}
