package extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.18, 1.20)")
@Extension
public class ClientTextureEventProvider {

    public static void willRegisterTextureFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.TextureRegistry> consumer) {
        NotificationCenterImpl.observer(TextureStitchEvent.Pre.class, consumer, event -> registryName -> {
            if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
                event.addSprite(registryName);
            }
        });
    }
}
