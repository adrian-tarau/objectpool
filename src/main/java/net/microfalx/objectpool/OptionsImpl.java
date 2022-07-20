package net.microfalx.objectpool;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledExecutorService;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

/**
 * Implementation of {@link ObjectPool.Options}.
 *
 * @param <T> the type of pooled objects
 */
final class OptionsImpl<T> implements ObjectPool.Options<T> {

    int minimum;
    int maximum = 10;
    Duration timeToLiveTimeout = ofMinutes(60);
    Duration abandonedTimeout = ofMinutes(60);
    Duration inactiveTimeout = ofSeconds(60);
    Duration maximumWait = ofSeconds(60);
    Duration maximumReuseTime = ofMinutes(15);
    int maximumReuseCount = Integer.MAX_VALUE;
    ObjectPool.Strategy strategy = ObjectPool.Strategy.LIFO;
    ScheduledExecutorService executor;
    ObjectFactory<T> factory;
    List<ObjectPool.Node> nodes = new ArrayList<>();

    @Override
    public List<ObjectPool.Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    @Override
    public int getMinimum() {
        return minimum;
    }

    @Override
    public int getMaximum() {
        return maximum;
    }

    @Override
    public Duration getTimeToLiveTimeout() {
        return timeToLiveTimeout;
    }

    @Override
    public Duration getAbandonedTimeout() {
        return abandonedTimeout;
    }

    @Override
    public Duration getInactiveTimeout() {
        return inactiveTimeout;
    }

    @Override
    public Duration getMaximumWait() {
        return maximumWait;
    }

    @Override
    public Duration getMaximumReuseTime() {
        return maximumReuseTime;
    }

    @Override
    public int getMaximumReuseCount() {
        return maximumReuseCount;
    }

    @Override
    public ObjectPool.Strategy getStrategy() {
        return strategy;
    }

    @Override
    public ObjectFactory<T> getFactory() {
        return factory;
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OptionsImpl.class.getSimpleName() + "[", "]")
                .add("minimum=" + minimum)
                .add("maximum=" + maximum)
                .add("timeToLiveTimeout=" + timeToLiveTimeout)
                .add("abandonedTimeout=" + abandonedTimeout)
                .add("inactiveTimeout=" + inactiveTimeout)
                .add("maximumWait=" + maximumWait)
                .add("maximumReuseTime=" + maximumReuseTime)
                .add("maximumReuseCount=" + maximumReuseCount)
                .add("strategy=" + strategy)
                .add("factory=" + factory)
                .add("executor=" + executor)
                .toString();
    }
}
