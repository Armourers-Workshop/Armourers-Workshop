package moe.plushie.armourers_workshop.init.provider;

import moe.plushie.armourers_workshop.api.common.IResourceManager;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.resources.ResourceManager;

public interface CommonNativeFactory {

    IResourceManager createResourceManager(ResourceManager resourceManager);

    MutableComponent createTranslatableComponent(String key, Object... args);
}
