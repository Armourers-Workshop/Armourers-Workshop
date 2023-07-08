package extensions.net.minecraft.server.packs.resources.ResourceManager;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.19)")
@Extension
public class Wrapper {

    public static IResourceManager asResourceManager(@This ResourceManager resourceManager) {
        return new IResourceManager() {
            @Override
            public boolean hasResource(ResourceLocation resourceLocation) {
                return resourceManager.hasResource(resourceLocation);
            }

            @Override
            public InputStream readResource(ResourceLocation resourceLocation) throws IOException {
                return resourceManager.getResource(resourceLocation).getInputStream();
            }

            @Override
            public void readResources(ResourceLocation target, Predicate<String> validator, BiConsumer<ResourceLocation, InputStream> consumer) {
                for (ResourceLocation key : resourceManager.listResources(target.getPath(), validator)) {
                    try {
                        if (!key.getNamespace().equals(target.getNamespace())) {
                            return;
                        }
                        for (Resource resource : resourceManager.getResources(key)) {
                            try {
                                InputStream inputStream = resource.getInputStream();
                                consumer.accept(key, inputStream);
                                inputStream.close();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };
    }
}
