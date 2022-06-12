package net.microfalx.binserde.objectpool;

import java.time.LocalDateTime;

/**
 * An interface implemented by pooled objects to allow to pool to detect abandoned objects.
 */
public interface UsageReporter {

    /**
     * Returns the time when the object as used last time.
     *
     * @return a non-null instance
     */
    LocalDateTime getLastUsedTime();
}
