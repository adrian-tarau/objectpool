package net.microfalx.binserde.objectpool;

import java.time.Duration;
import java.util.StringJoiner;

/**
 * Implementation of {@link ObjectPool.Options}.
 *
 * @param <T> the type of pooled objects
 */
class OptionsImpl<T> implements ObjectPool.Options<T> {

    int minimum;
    int maximum;
    Duration timeToLiveTimeout;
    Duration abandonedTimeout;
    Duration inactiveTimeout;
    Duration maximumWait;
    Duration maximumReuseTime;
    int maximumReuseCount;
    Strategy strategy = Strategy.LIFO;
    ObjectFactory<T> factory;

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
    public Strategy getStrategy() {
        return strategy;
    }

    @Override
    public ObjectFactory<T> getFactory() {
        return factory;
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
                .toString();
    }
}
