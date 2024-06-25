package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.server.packs.resources.ResourceManager;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.core.IResourceManager;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
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
            public boolean hasResource(IResourceLocation location) {
                return resourceManager.getResource(location.toLocation()).isPresent();
            }

            @Override
            public IResource readResource(IResourceLocation location) throws IOException {
                Optional<Resource> resource = resourceManager.getResource(location.toLocation());
                if (resource.isPresent()) {
                    return wrap(location, resource.get());
                }
                throw new FileNotFoundException(location.toString());
            }

            @Override
            public void readResources(IResourceLocation target, Predicate<String> validator, BiConsumer<IResourceLocation, IResource> consumer) {
                resourceManager.listResources(target.getPath(), rl -> validator.test(rl.getPath())).forEach((key, resource) -> {
                    try {
                        try {
                            if (!key.getNamespace().equals(target.getNamespace())) {
                                return;
                            }
                            IResourceLocation key1 = OpenResourceLocation.create(key);
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
