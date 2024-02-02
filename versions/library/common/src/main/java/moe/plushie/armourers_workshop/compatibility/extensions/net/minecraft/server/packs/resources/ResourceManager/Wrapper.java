package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.server.packs.resources.ResourceManager;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IResource;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.19, )")
@Extension
public class Wrapper {

    public static IResourceManager asResourceManager(@This ResourceManager resourceManager) {
        return new IResourceManager() {
            @Override
            public boolean hasResource(ResourceLocation resourceLocation) {
                return resourceManager.getResource(resourceLocation).isPresent();
            }

            @Override
            public IResource readResource(ResourceLocation resourceLocation) throws IOException {
                Optional<Resource> resource = resourceManager.getResource(resourceLocation);
                if (resource.isPresent()) {
                    return wrap(resourceLocation, resource.get());
                }
                throw new FileNotFoundException(resourceLocation.toString());
            }

            @Override
            public void readResources(ResourceLocation target, Predicate<String> validator, BiConsumer<ResourceLocation, IResource> consumer) {
                resourceManager.listResources(target.getPath(), rl -> validator.test(rl.getPath())).forEach((key, resource) -> {
                    try {
                        try {
                            if (!key.getNamespace().equals(target.getNamespace())) {
                                return;
                            }
                            consumer.accept(key, wrap(key, resource));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            }

            private IResource wrap(ResourceLocation name, Resource resource) {

                return new IResource() {
                    @Override
                    public String getName() {
                        return name.toString();
                    }

                    @Override
                    public String getSource() {
                        return resource.sourcePackId();
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return resource.open();
                    }
                };
            }
        };
    }
}
