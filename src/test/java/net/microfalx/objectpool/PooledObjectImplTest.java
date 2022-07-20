package net.microfalx.objectpool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PooledObjectImplTest {

    private ObjectPool<String> objectPool;

    @BeforeEach
    void before() {
        objectPool = Mockito.mock(ObjectPool.class);
    }

    @Test
    void state() {
        PooledObjectImpl<String> object = new PooledObjectImpl<>(objectPool, "Demo");
        assertEquals(PooledObject.State.IDLE, object.getState());
    }


}