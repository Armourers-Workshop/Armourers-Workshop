package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.init.provider.CommonNativeFactory;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import net.minecraft.server.packs.resources.ResourceManager;

public class CommonNativeManager {

    @ExpectPlatform
    public static CommonNativeFactory getFactory() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonNativeProvider getProvider() {
        throw new AssertionError();
    }

    public static IResourceManager createResourceManager(ResourceManager resourceManager) {
        return getFactory().createResourceManager(resourceManager);
    }
}
