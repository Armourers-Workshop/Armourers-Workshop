package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractClientNativeProviderExt_V19;
import moe.plushie.armourers_workshop.compatibility.fabric.ext.AbstractClientFabricExt_V18;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class AbstractFabricClientNativeImpl extends AbstractClientNativeImpl implements AbstractFabricClientNativeProvider, AbstractClientFabricExt_V18, AbstractClientNativeProviderExt_V19 {

    public static ItemStack TOOLTIP_ITEM_STACK = ItemStack.EMPTY;

    @Override
    public void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
    }

    @Override
    public void willRegisterTexture(Consumer<TextureRegistry> consumer) {
        // everything in the block, item, particle and a few other folders is now stitched automatically.
    }
}
