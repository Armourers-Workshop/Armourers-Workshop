package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, 26)")
@Extension
public class TintSourceExt {

    public static void registerItemTintSourceFA(@ThisClass Class<?> clazz, IRegistryHolder<? extends Item> item, AbstractItemTintSource source) {
        ColorProviderRegistry.ITEM.register((itemStack, index) -> source.calculate(itemStack, null, null, index), item.get());
    }

    public static void registerBlockTintSourceFA(@ThisClass Class<?> clazz, IRegistryHolder<? extends Block> block, AbstractBlockTintSource source) {
        ColorProviderRegistry.BLOCK.register((blockState, level, blockPos, index) -> source.calculate(blockState, level, blockPos, index), block.get());
    }
}
