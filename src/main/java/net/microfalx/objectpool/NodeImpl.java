package net.microfalx.objectpool;

import java.net.URI;

import static net.microfalx.lang.ArgumentUtils.requireNonNull;

class NodeImpl implements ObjectPool.Node {

    private final URI uri;

    NodeImpl(URI uri) {
        requireNonNull(uri);
        this.uri = uri;
    }

    @Override
    public String getName() {
        return uri.getHost();
    }

    @Override
    public URI getUri() {
        return uri;
    }
}
