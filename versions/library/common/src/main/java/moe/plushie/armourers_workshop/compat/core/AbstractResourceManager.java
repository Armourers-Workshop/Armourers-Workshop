package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResource;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Available("[19, )")
public class AbstractResourceManager implements OpenResourceManager {

    private final ResourceManager resourceManager;

    public AbstractResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public static AbstractResourceManager wrap(ResourceManager resourceManager) {
        return new AbstractResourceManager(resourceManager);
    }

    public static ResourceManager unwrap(OpenResourceManager resourceManager) {
        return ((AbstractResourceManager) resourceManager).resourceManager;
    }

    @Override
    public boolean hasResource(OpenResourceKey key) {
        return resourceManager.getResource(key.get()).isPresent();
    }

    @Override
    public OpenResource readResource(OpenResourceKey key) throws IOException {
        var resource = resourceManager.getResource(key.get());
        if (resource.isPresent()) {
            return wrap(key, resource.get());
        }
        throw new FileNotFoundException(key.toString());
    }

    @Override
    public void listResources(OpenResourceKey target, Predicate<String> validator, BiConsumer<OpenResourceKey, OpenResource> consumer) {
        resourceManager.listResources(target.path(), rl -> validator.test(rl.getPath())).forEach((key, resource) -> {
            try {
                try {
                    if (!key.getNamespace().equals(target.namespace())) {
                        return;
                    }
                    var key1 = AbstractResourceKey.wrap(key);
                    consumer.accept(key1, wrap(key1, resource));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private OpenResource wrap(OpenResourceKey name, Resource resource) {
        return new OpenResource() {
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
