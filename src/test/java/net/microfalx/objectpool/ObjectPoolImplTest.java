package net.microfalx.objectpool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
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
                .node(URI.create("tcp://localhost"))
                .strategy(ObjectPool.Strategy.FIFO)
                .timeToLiveTimeout(ofMinutes(30)).abandonedTimeout(ofMinutes(40)).inactiveTimeout(ofSeconds(50))
                .maximumReuseTime(ofMinutes(5)).maximumReuseCount(100)
                .maximumWait(ofSeconds(25))
                .build();
        ObjectPool.Options<Integer> options = objectPool.getOptions();
        assertNotNull(options);
        assertEquals(1, options.getNodes().size());
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
    void getMetrics() {
        Integer object = objectPool.borrowObject();
        ObjectPool.Metrics metrics = objectPool.getMetrics();
        assertEquals(1, metrics.getBorrowedCount());
    }

    @Test
    void addObject() {
        assertEquals(0, objectPool.getSize());
        objectPool.addObject();
        assertEquals(1, objectPool.getSize());
        assertEquals(1, objectPool.getSize(PooledObject.State.IDLE));
    }

    @Test
    void borrowObject() {
        assertEquals(0, objectPool.getSize());
        objectPool.borrowObject();
        assertEquals(1, objectPool.getSize());
        assertEquals(1, objectPool.getSize(PooledObject.State.ACTIVE));
    }

    @Test
    void returnObject() {
        Integer object = objectPool.borrowObject();
        objectPool.returnObject(object);
        assertEquals(1, objectPool.getSize());
        assertEquals(1, objectPool.getSize(PooledObject.State.IDLE));
    }

    @Test
    void invalidateObject() {
        Integer object = objectPool.borrowObject();
        objectPool.invalidateObject(object);
        assertEquals(0, objectPool.getSize());
    }

    @Test
    void clear() {
        Integer object = objectPool.borrowObject();
        objectPool.clear();
        assertEquals(1, objectPool.getSize());
        objectPool.returnObject(object);
        objectPool.clear();
        assertEquals(0, objectPool.getSize());
    }

    @Test
    void close() {
        assertFalse(objectPool.isClosed());
        objectPool.borrowObject();
        objectPool.close();
        assertEquals(0, objectPool.getSize());
        assertTrue(objectPool.isClosed());
        Assertions.assertThrowsExactly(ObjectPoolException.class, () -> objectPool.borrowObject());
    }

    @Test
    void getSize() {
        Integer object = objectPool.borrowObject();
        assertEquals(1, objectPool.getSize());
        assertEquals(1, objectPool.getSize(PooledObject.State.ACTIVE));
        objectPool.returnObject(object);
        assertEquals(1, objectPool.getSize(PooledObject.State.IDLE));
    }

    @Test
    void getObjects() {
        Integer object = objectPool.borrowObject();
        assertEquals(1, objectPool.getObjects().size());
        assertEquals(1, objectPool.getObjects(PooledObject.State.ACTIVE).size());
        objectPool.returnObject(object);
        assertEquals(1, objectPool.getObjects(PooledObject.State.IDLE).size());
    }

    private static class IntegerObjectFactory implements ActivableObjectFactory<Integer> {

        private AtomicInteger counter = new AtomicInteger(1);

        @Override
        public Integer makeObject(ObjectPool<Integer> pool) throws Exception {
            return counter.getAndIncrement();
        }

        @Override
        public void destroyObject(ObjectPool<Integer> pool, Integer object) throws Exception {
            //  do nothing
        }

        @Override
        public void activateObject(ObjectPool<Integer> pool, PooledObject<Integer> object) throws Exception {

        }

        @Override
        public void deactivateObject(ObjectPool<Integer> pool, PooledObject<Integer> object) throws Exception {

        }
    }
}