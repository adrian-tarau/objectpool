package net.microfalx.binserde.objectpool;

import java.time.Duration;
import java.util.Collection;

import static net.microfalx.binserde.objectpool.ObjectPoolUtils.*;

/**
 * An object pool.
 *
 * @param <T> the type of pooled objects
 */
public interface ObjectPool<T> {

    /**
     * Creates a builder used to initialize and create the pool.
     *
     * @param <T> the type of pooled objects
     * @return the builder
     */
    static <T> Builder<T> create(ObjectFactory<T> factory) {
        return new Builder<>(factory);
    }

    /**
     * Returns the configuration options of this pool.
     *
     * @return a non-null instance
     */
    Options<T> getOptions();

    /**
     * Creates a new object and adds it to the pool.
     */
    void addObject();

    /**
     * Borrows an instance from this pool.
     */
    T borrowObject();

    /**
     * Returns an instance to the pool.
     *
     * @param object
     */
    void returnObject(T object);

    /**
     * Invalidates an object from the pool.
     * <p>
     * The object will be scheduled to be invalidated and removed from the pool.
     *
     * @param object a pooled object
     */
    void invalidateObject(T object);

    /**
     * Clears any objects sitting idle in the pool.
     * <p>
     * Any released objects will be release also associated resources.
     */
    void clear();

    /**
     * Closes this pool.
     * <p>
     * Any pooled object and their resources will be released.
     */
    void close();

    /**
     * Returns the number of pooled objects in a given state.
     *
     * @param state the state of pooled objects
     * @return a positive integer
     */
    int getSize(State state);

    /**
     * Returns the number of pooled objects in any state.
     *
     * @return a positive integer
     */
    int getSize();

    /**
     * Returns the pooled objects in a given state.
     *
     * @param state the state of pooled objects
     * @return a non-null collection
     */
    Collection<PooledObject<T>> getObjects(State state);

    /**
     * Holds options for an object pool.
     */
    interface Options<T> {

        /**
         * Returns the minimum number of objects preserved in the pool.
         * <p>
         * The pool will always keep this number of objects always available in the pool once they were created.
         * <p>
         * This parameter might be useful in case of very expensive objects to make sure a <code>reserved</code>
         * number of objects are always available.
         *
         * @return a positive integer
         */
        int getMinimum();

        /**
         * Returns the maximum number of objects allowed to be created and maintained by the pool.
         *
         * @return a positive integer
         */
        int getMaximum();

        /**
         * Returns the time-to-live timeout.
         * <p>
         * The time-to-live  timeout enables borrowed objects to remain borrowed for a specific amount of time before
         * the object is reclaimed by the pool.
         *
         * @return a positive duration
         */
        Duration getTimeToLiveTimeout();

        /**
         * Return abandoned timeout.
         * <p>
         * The abandoned timeout enables borrowed object to be reclaimed back into the  pool after an object
         * has not been used for a specific amount of time.
         * <p>
         * Abandonment is determined by monitoring calls to the objects. Objects must implement {@link  UsageReporter} to
         * be able to apply this timeout.
         *
         * @return a positive duration
         */
        Duration getAbandonedTimeout();

        /**
         * Returns the inactive timeout.
         * <p>
         * The inactive timeout specifies how long an available object can remain idle before it is closed and
         * removed from the pool. This timeout is only applicable to idle objects and does not affect borrowed objects.
         *
         * @return a positive duration
         */
        Duration getInactiveTimeout();

        /**
         * Returns the maximum amount of time a client will wait for an object o become available in the pool.
         *
         * @return a positive duration
         */
        Duration getMaximumWait();

        /**
         * Returns the maximum amount of time an object is allowed to stay in the pool (used or idle).
         * <p>
         * This timeout allows for object recycling based on time. Sometimes objects hold/accumulate resources
         * in various services and limiting the amount of time they are kept alive helps with resource management.
         *
         * @return a positive duration
         */
        Duration getMaximumReuseTime();

        /**
         * Returns the maximum number of borrow operations are allowed for a pooled object.
         *
         * @return a positiver integer
         * @see #getMaximumReuseTime()
         */
        int getMaximumReuseCount();

        /**
         * Returns the borrow strategy.
         *
         * @return a non-null enum
         */
        Strategy getStrategy();

        /**
         * Returns the factory used to create and destroy objects.
         *
         * @return a non-null instance
         */
        ObjectFactory<T> getFactory();

    }

    /**
     * A builder for an object pool.
     *
     * @param <T>the type of pooled objects
     */
    class Builder<T> {

        private OptionsImpl options;

        private Builder(ObjectFactory<T> factory) {
            options.factory = requireNonNull(factory);
        }

        public Builder<T> minimum(int minimum) {
            options.minimum = requireBounded(minimum, 0, MAXIMUM_POOL_SIZE);
            return this;
        }

        public Builder<T> maximum(int maximum) {
            options.maximum = requireBounded(maximum, 0, MAXIMUM_POOL_SIZE);
            return this;
        }

        public Builder<T> timeToLiveTimeout(Duration timeToLiveTimeout) {
            options.timeToLiveTimeout = timeToLiveTimeout;
            return this;
        }

        public Builder<T> abandonedTimeout(Duration abandonedTimeout) {
            options.abandonedTimeout = abandonedTimeout;
            return this;
        }

        public Builder<T> inactiveTimeout(Duration inactiveTimeout) {
            options.inactiveTimeout = inactiveTimeout;
            return this;
        }

        public Builder<T> maximumWait(Duration maximumWait) {
            options.maximumWait = maximumWait;
            return this;
        }

        public Builder<T> maximumReuseTime(Duration maximumReuseTime) {
            options.maximumReuseTime = maximumReuseTime;
            return this;
        }

        public Builder<T> maximumReuseCount(int maximumReuseCount) {
            options.maximumReuseCount = maximumReuseCount;
            return this;
        }

        public Builder<T> strategy(Strategy strategy) {
            requireNonNull(strategy);
            options.strategy = strategy;
            return this;
        }

        public ObjectPool<T> build() {
            ObjectPoolImpl<T> pool = new ObjectPoolImpl<>(options);
            return pool;
        }
    }
}
