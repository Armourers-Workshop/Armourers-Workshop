package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class OpenResourceLocation implements IResourceLocation, Comparable<OpenResourceLocation> {

    private final String namespace;
    private final String path;

    private ResourceLocation resourceLocation;

    private OpenResourceLocation(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static OpenResourceLocation create(ResourceLocation location) {
        var newValue = new OpenResourceLocation(location.getNamespace(), location.getPath());
        newValue.resourceLocation = location;
        return newValue;
    }

    public static OpenResourceLocation create(String namespace, String path) {
        return new OpenResourceLocation(namespace, path);
    }

    public static OpenResourceLocation parse(String id) {
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
        if (!(object instanceof OpenResourceLocation that)) return false;
        return namespace.equals(that.namespace) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public int compareTo(OpenResourceLocation resourceLocation) {
        int i = path.compareTo(resourceLocation.path);
        if (i == 0) {
            i = namespace.compareTo(resourceLocation.namespace);
        }
        return i;
    }

    @Override
    public OpenResourceLocation withPath(String path) {
        return create(namespace, path);
    }

    @Override
    public OpenResourceLocation withNamespace(String namespace) {
        return create(namespace, path);
    }

    @Override
    public ResourceLocation toLocation() {
        if (resourceLocation == null) {
            resourceLocation = IResourceLocation.super.toLocation();
        }
        return resourceLocation;
    }
}
