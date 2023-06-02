package extensions.net.minecraft.core.Registry;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.renderer.item.ItemProperties;

import java.util.function.Consumer;

@Available("[1.20, )")
@Extension
public class ClientEventProvider {

    public static void willRegisterItemPropertyFA(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.ItemPropertyRegistry> consumer) {
        consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
    }

    public static void willRegisterTextureFA(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.TextureRegistry> consumer) {
        // everything in the block, item, particle and a few other folders is now stitched automatically.
    }
}
