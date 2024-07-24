package moe.plushie.armourers_workshop.compatibility.forge.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractItemStackRendererProvider;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class ForgeItemRegistry {

    public static void registerItemRendererFO(@ThisClass Class<?> clazz, IRegistryHolder<? extends Item> item, AbstractItemStackRendererProvider provider) {
        AbstractForgeClientEventsImpl.CLIENT_EXTENSIONS_REGISTRY.listen(event -> {
            var renderer = provider.create();
            var extensions = new AbstractForgeItemRenderer() {

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return renderer;
                }
            };
            event.registerItem(extensions, item.get());
        });
    }
}
