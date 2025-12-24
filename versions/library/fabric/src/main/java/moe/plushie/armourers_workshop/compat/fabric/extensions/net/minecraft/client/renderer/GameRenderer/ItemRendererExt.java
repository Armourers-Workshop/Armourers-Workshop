package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.client.renderer.item.AbstractItemSpecialRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.item.Item;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, 1.22)")
@Extension
public class ItemRendererExt {

    public static void registerItemSpecialRendererFA(@ThisClass Class<?> clazz, IRegistryHolder<? extends Item> item, AbstractItemSpecialRenderer provider) {
        var renderer = provider.bake(null);
        BuiltinItemRendererRegistry.INSTANCE.register(item.get(), renderer::renderByItem);
    }
}

