package net.microfalx.objectpool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.*;

class ObjectPoolImplTest {

    private ObjectPool<Integer> objectPool;

    @BeforeEach
    void setup() {
        objectPool = ObjectPool.create(new IntegerObjectFactory()).build();
    }

    @Test
    void getDefaultOptions() {
        ObjectPool.Options<Integer> options = objectPool.getOptions();
        assertNotNull(options);
        assertSame(IntegerObjectFactory.class, options.getFactory().getClass());
        assertEquals(0, options.getMinimum());
        assertEquals(10, options.getMaximum());
        assertEquals(ObjectPool.Strategy.LIFO, options.getStrategy());
        assertEquals(ofMinutes(60), options.getTimeToLiveTimeout());
        assertEquals(ofMinutes(60), options.getAbandonedTimeout());
        assertEquals(ofSeconds(60), options.getInactiveTimeout());
        assertEquals(ofSeconds(60), options.getMaximumWait());
        assertEquals(ofMinutes(15), options.getMaximumReuseTime());
        assertEquals(Integer.MAX_VALUE, options.getMaximumReuseCount());
        assertNotNull(options.toString());
    }

    @Test
    void getCustomOptions() {
        objectPool = ObjectPool.create(new IntegerObjectFactory()).minimum(2).maximum(15)
                .strategy(ObjectPool.Strategy.FIFO)
                .timeToLiveTimeout(ofMinutes(30)).abandonedTimeout(ofMinutes(40)).inactiveTimeout(ofSeconds(50))
                .maximumReuseTime(ofMinutes(5)).maximumReuseCount(100)
                .maximumWait(ofSeconds(25))
                .build();
        ObjectPool.Options<Integer> options = objectPool.getOptions();
        assertNotNull(options);
        assertEquals(2, options.getMinimum());
        assertEquals(15, options.getMaximum());
        assertEquals(ObjectPool.Strategy.FIFO, options.getStrategy());
        assertEquals(ofMinutes(30), options.getTimeToLiveTimeout());
        assertEquals(ofMinutes(40), options.getAbandonedTimeout());
        assertEquals(ofSeconds(50), options.getInactiveTimeout());
        assertEquals(ofSeconds(25), options.getMaximumWait());
        assertEquals(ofMinutes(5), options.getMaximumReuseTime());
        assertEquals(100, options.getMaximumReuseCount());
        assertNotNull(options.toString());
    }

    @Test
    void addObject() {
    }

    @Test
    void borrowObject() {
    }

    @Test
    void returnObject() {
    }

    @Test
    void invalidateObject() {
    }

    @Test
    void clear() {
    }

    @Test
    void close() {
    }

    @Test
    void getSize() {
    }

    @Test
    void testGetSize() {
    }

    @Test
    void getObjects() {
    }

    private static class IntegerObjectFactory implements ObjectFactory<Integer> {

        private AtomicInteger counter = new AtomicInteger(1);

        @Override
        public Integer makeObject() throws Exception {
            return counter.getAndIncrement();
        }

        @Override
        public void destroyObject(Integer object) throws Exception {
            //  do nothing
        }
    }
}