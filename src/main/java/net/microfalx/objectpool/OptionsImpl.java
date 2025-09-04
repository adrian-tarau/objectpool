package net.microfalx.objectpool;

import net.microfalx.lang.NamedAndTaggedIdentifyAware;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

/**
 * Implementation of {@link ObjectPool.Options}.
 *
 * @param <T> the type of pooled objects
 */
public class OptionsImpl<T> extends NamedAndTaggedIdentifyAware<String> implements ObjectPool.Options<T> {

    int minimum;
    int maximum = 10;
    Duration timeToLiveTimeout = ofMinutes(60);
    Duration abandonedTimeout = ofMinutes(60);
    Duration inactiveTimeout = ofSeconds(60);
    Duration connectionTimeout = ofSeconds(10);
    Duration maximumWait = ofSeconds(60);
    Duration maximumReuseTime = ofMinutes(15);
    int maximumReuseCount = Integer.MAX_VALUE;
    ObjectPool.Strategy strategy = ObjectPool.Strategy.LIFO;
    ScheduledExecutorService executor;
    ObjectFactory<T> factory;
    List<ObjectPool.Node> nodes = new ArrayList<>();

    public OptionsImpl() {
        setId(UUID.randomUUID().toString());
        setName("Unnamed");
    }

    void updateId(String id) {
        setId(id);
    }

    void updateName(String name) {
        setName(name);
    }

    void updateDescription(String description) {
        setDescription(description);
    }

    @Override
    public final List<ObjectPool.Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    @Override
    public final int getMinimum() {
        return minimum;
    }

    @Override
    public final int getMaximum() {
        return maximum;
    }

    @Override
    public final Duration getTimeToLiveTimeout() {
        return timeToLiveTimeout;
    }

    @Override
    public final Duration getAbandonedTimeout() {
        return abandonedTimeout;
    }

    @Override
    public final Duration getInactiveTimeout() {
        return inactiveTimeout;
    }

    @Override
    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public final Duration getMaximumWait() {
        return maximumWait;
    }

    @Override
    public final Duration getMaximumReuseTime() {
        return maximumReuseTime;
    }

    @Override
    public final int getMaximumReuseCount() {
        return maximumReuseCount;
    }

    @Override
    public final ObjectPool.Strategy getStrategy() {
        return strategy;
    }

    @Override
    public final ObjectFactory<T> getFactory() {
        return factory;
    }

    @Override
    public final ScheduledExecutorService getExecutor() {
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
