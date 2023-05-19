package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(value = EnvType.CLIENT)
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
        return Minecraft.getInstance().getResourceManager().asResourceManager();
    }
}
