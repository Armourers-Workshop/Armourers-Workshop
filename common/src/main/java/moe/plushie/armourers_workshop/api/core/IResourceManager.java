package moe.plushie.armourers_workshop.api.core;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface IResourceManager {

    boolean hasResource(IResourceKey key);

    IResource readResource(IResourceKey key) throws IOException;

    void readResources(IResourceKey target, Predicate<String> validator, BiConsumer<IResourceKey, IResource> consumer);
}
