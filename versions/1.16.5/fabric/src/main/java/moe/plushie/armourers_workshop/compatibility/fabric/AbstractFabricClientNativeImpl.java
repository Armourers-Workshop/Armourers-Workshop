package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractClientNativeExt_V1618;
import moe.plushie.armourers_workshop.compatibility.fabric.ext.AbstractClientFabricExt_V16;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Consumer;

public class AbstractFabricClientNativeImpl extends AbstractClientNativeImpl implements AbstractFabricClientNativeProvider, AbstractClientFabricExt_V16, AbstractClientNativeExt_V1618 {

    @Override
    public void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        consumer.accept(((registryName, item, property) -> FabricModelPredicateProviderRegistry.register(item, registryName, (itemStack, level, entity) -> property.getValue(itemStack, level, entity, 0))));
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
