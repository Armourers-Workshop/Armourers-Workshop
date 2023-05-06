package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.provider.FabricClientNativeProvider;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Consumer;

public class AbstractFabricClientNativeImpl extends AbstractClientNativeImpl implements FabricClientNativeProvider, AbstractFabricClientNativeProvider {

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
