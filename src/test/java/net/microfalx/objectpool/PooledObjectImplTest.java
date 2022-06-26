package net.microfalx.objectpool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PooledObjectImplTest {

    @Test
    void state() {
        PooledObjectImpl<String> object = new PooledObjectImpl<>("Demo");
        assertEquals(PooledObject.State.IDLE, object.getState());
    }


}