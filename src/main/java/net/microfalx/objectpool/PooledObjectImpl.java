package net.microfalx.objectpool;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Duration.ofNanos;

/**
 * Implementation of {@link  PooledObject}.
 *
 * @param <T> the type of pooled objects
 */
final class PooledObjectImpl<T> implements PooledObject<T> {

    private final long created = System.currentTimeMillis();
    private volatile long lastBorrowed;
    private volatile long lastReturned;
    private volatile long lastUsed;

    private final AtomicLong borrowedCounter = new AtomicLong();
    private final AtomicLong borrowedDuration = new AtomicLong();
    private final AtomicLong totalBorrowedDuration = new AtomicLong();

    private final AtomicLong idleCounter = new AtomicLong();
    private final AtomicLong idleDuration = new AtomicLong();
    private final AtomicLong totalIdleDuration = new AtomicLong();

    private volatile T object;

    @Override
    public State getState() {
        return null;
    }

    @Override
    public ZonedDateTime getCreatedTime() {
        return created > 0 ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()) : null;
    }

    @Override
    public ZonedDateTime getLastBorrowedTime() {
        return lastBorrowed > 0 ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastBorrowed), ZoneId.systemDefault()) : null;
    }

    @Override
    public ZonedDateTime getLastReturnedTime() {
        return lastReturned > 0 ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastReturned), ZoneId.systemDefault()) : null;
    }

    @Override
    public ZonedDateTime getLastUsedTime() {
        return lastUsed > 0 ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastUsed), ZoneId.systemDefault()) : null;
    }

    @Override
    public long getBorrowedCount() {
        return borrowedCounter.get();
    }

    @Override
    public Duration getBorrowedDuration() {
        return ofNanos(borrowedDuration.get());
    }

    @Override
    public Duration getTotalBorrowedDuration() {
        return ofNanos(totalBorrowedDuration.get());
    }

    @Override
    public Duration getIdleDuration() {
        return ofNanos(idleDuration.get());
    }

    @Override
    public Duration getTotalIdleDuration() {
        return ofNanos(totalIdleDuration.get());
    }

    @Override
    public T get() {
        return object;
    }
}
