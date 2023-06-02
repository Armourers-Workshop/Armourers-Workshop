package extensions.net.minecraft.core.Registry;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

import java.util.function.Consumer;

@Available("[1.20, )")
@Extension
public class ClientTextureEventProvider {

    public static void willRegisterTextureFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.TextureRegistry> consumer) {
        // everything in the block, item, particle and a few other folders is now stitched automaticall.
    }
}
