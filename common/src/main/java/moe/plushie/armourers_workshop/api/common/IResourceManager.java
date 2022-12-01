package moe.plushie.armourers_workshop.api.common;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public interface IResourceManager {

    boolean hasResource(ResourceLocation resourceLocation);

    InputStream readResource(ResourceLocation resourceLocation) throws IOException;
}
