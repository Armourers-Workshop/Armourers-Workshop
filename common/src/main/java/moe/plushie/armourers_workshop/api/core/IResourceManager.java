package moe.plushie.armourers_workshop.api.core;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface IResourceManager {

    boolean hasResource(IResourceLocation location);

    IResource readResource(IResourceLocation location) throws IOException;

    void readResources(IResourceLocation target, Predicate<String> validator, BiConsumer<IResourceLocation, IResource> consumer);
}
