package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IResourceKey;

public class OpenResourceKey implements IResourceKey, Comparable<OpenResourceKey> {

    public static final IDataCodec<OpenResourceKey> CODEC = IDataCodec.STRING.xmap(OpenResourceKey::parse, OpenResourceKey::toString);

    private final String namespace;
    private final String path;

    private OpenResourceKey(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static OpenResourceKey create(String namespace, String path) {
        return new OpenResourceKey(namespace, path);
    }

    public static OpenResourceKey withDefaultNamespace(String path) {
        return create("minecraft", path);
    }

    public static OpenResourceKey parse(String id) {
        int i = id.indexOf(':');
        String namespace = "minecraft";
        String path;
        if (i >= 0) {
            path = id.substring(i + 1);
            if (i != 0) {
                namespace = id.substring(0, i);
            }
        } else {
            path = id;
        }
        return create(namespace, path);
    }

    public static OpenResourceKey of(IResourceKey key) {
        if (key instanceof OpenResourceKey key1) {
            return key1;
        }
        return new OpenResourceKey(key.namespace(), key.path());
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof OpenResourceKey that)) return false;
        return namespace.equals(that.namespace) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public int compareTo(OpenResourceKey key) {
        int i = path.compareTo(key.path);
        if (i == 0) {
            i = namespace.compareTo(key.namespace);
        }
        return i;
    }

    @Override
    public OpenResourceKey withPath(String path) {
        return create(namespace, path);
    }

    @Override
    public OpenResourceKey withNamespace(String namespace) {
        return create(namespace, path);
    }
}
