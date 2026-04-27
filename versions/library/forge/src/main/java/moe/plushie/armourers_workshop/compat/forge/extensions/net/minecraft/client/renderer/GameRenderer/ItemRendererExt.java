package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.client.renderer.item.AbstractItemSpecialRenderer;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeItemStackRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[21, 26)")
@Extension
public class ItemRendererExt {

    public static void registerItemSpecialRendererFO(@ThisClass Class<?> clazz, IRegistryHolder<? extends Item> item, AbstractItemSpecialRenderer provider) {
        AbstractForgeClientEventsImpl.CLIENT_EXTENSIONS_REGISTRY.listen(event -> {
            var renderer = provider.bake(null);
            var extensions = new AbstractForgeItemStackRenderer() {

                @Override
                public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                    return renderer;
                }
            };
            event.registerItem(extensions, item.get());
        });
    }
}
