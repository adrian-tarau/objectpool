package net.microfalx.objectpool;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeImplTest {

    @Test
    void create() {
        NodeImpl node = new NodeImpl(URI.create("tcp://localhost"));
        assertEquals("localhost", node.getName());
        assertEquals("tcp://localhost", node.getUri().toASCIIString());
    }

}