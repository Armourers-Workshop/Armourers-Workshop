package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.core.IResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Available("[1.19, )")
public class AbstractResourceManager implements IResourceManager {

    private final ResourceManager resourceManager;

    public AbstractResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public static AbstractResourceManager wrap(ResourceManager resourceManager) {
        return new AbstractResourceManager(resourceManager);
    }

    public static ResourceManager unwrap(IResourceManager resourceManager) {
        return ((AbstractResourceManager) resourceManager).resourceManager;
    }

    @Override
    public boolean hasResource(IResourceLocation location) {
        return resourceManager.getResource(location.get()).isPresent();
    }

    @Override
    public IResource readResource(IResourceLocation location) throws IOException {
        var resource = resourceManager.getResource(location.get());
        if (resource.isPresent()) {
            return wrap(location, resource.get());
        }
        throw new FileNotFoundException(location.toString());
    }

    @Override
    public void readResources(IResourceLocation target, Predicate<String> validator, BiConsumer<IResourceLocation, IResource> consumer) {
        resourceManager.listResources(target.path(), rl -> validator.test(rl.getPath())).forEach((key, resource) -> {
            try {
                try {
                    if (!key.getNamespace().equals(target.namespace())) {
                        return;
                    }
                    var key1 = AbstractResourceLocation.wrap(key);
                    consumer.accept(key1, wrap(key1, resource));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private IResource wrap(IResourceLocation name, Resource resource) {
        return new IResource() {
            @Override
            public String name() {
                return name.toString();
            }

            @Override
            public String source() {
                return resource.sourcePackId();
            }

            @Override
            public InputStream inputStream() throws IOException {
                return resource.open();
            }
        };
    }
}
