package net.microfalx.objectpool;

import java.net.URI;

class NodeImpl implements ObjectPool.Node {

    private final URI uri;

    NodeImpl(URI uri) {
        ObjectPoolUtils.requireNonNull(uri);
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
