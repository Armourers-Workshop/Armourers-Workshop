package moe.plushie.armourers_workshop.compatibility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public interface AbstractCommonFactory {

    static InputStream createInputStream(ResourceManager resourceManager, ResourceLocation resourceLocation) throws IOException {
        Resource resource = resourceManager.getResource(resourceLocation);
        return resource.getInputStream();
    }

}
