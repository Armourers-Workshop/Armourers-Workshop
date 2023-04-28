package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractClientNativeProviderExt_V19;
import moe.plushie.armourers_workshop.compatibility.fabric.ext.AbstractClientFabricExt_V18;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Consumer;

public class AbstractFabricClientNativeImpl extends AbstractClientNativeImpl implements AbstractFabricClientNativeProvider, AbstractClientFabricExt_V18, AbstractClientNativeProviderExt_V19 {

    @Override
    public void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
    }

    @Override
    public void willRegisterTexture(Consumer<TextureRegistry> consumer) {
        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register((atlas, registry) -> {
            if (atlas.location().equals(InventoryMenu.BLOCK_ATLAS)) {
                consumer.accept(registry::register);
            }
        });
    }
}
