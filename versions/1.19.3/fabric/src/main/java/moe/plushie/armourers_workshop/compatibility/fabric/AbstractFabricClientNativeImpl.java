package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.compatibility.v19.ClientNativeProviderExt_V1920;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class AbstractFabricClientNativeImpl extends AbstractClientNativeImpl implements AbstractFabricClientNativeProvider, ClientNativeProviderExt_V1920 {

    public static ItemStack TOOLTIP_ITEM_STACK = ItemStack.EMPTY;

    @Override
    public void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
    }

    @Override
    public void willRegisterTexture(Consumer<TextureRegistry> consumer) {
        // FIXME: @SAGESSE, to use assets/modid/atlases
    }
}
