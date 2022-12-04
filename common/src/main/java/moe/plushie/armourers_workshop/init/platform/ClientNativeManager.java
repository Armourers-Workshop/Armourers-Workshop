package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

public class ClientNativeManager {

    @ExpectPlatform
    public static ClientNativeFactory getFactory() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ClientNativeProvider getProvider() {
        throw new AssertionError();
    }

    public static IResourceManager getResourceManager() {
        return getFactory().getResourceManager();
    }

    public static IBufferBuilder createBuilderBuffer(int size) {
        return getFactory().createBuilderBuffer(size);
    }
}
