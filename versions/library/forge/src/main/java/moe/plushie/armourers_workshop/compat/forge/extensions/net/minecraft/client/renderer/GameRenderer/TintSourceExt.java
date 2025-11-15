package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, 1.22)")
@Extension
public class TintSourceExt {

    public static void registerItemTintSourceFO(@ThisClass Class<?> clazz, IRegistryHolder<? extends Item> item, AbstractItemTintSource source) {
        AbstractForgeClientEventsImpl.ITEM_COLOR_REGISTRY.listen(event -> event.getItemColors().register((itemStack, index) -> source.calculate(itemStack, null, null, index), item.get()));
    }

    public static void registerBlockTintSourceFO(@ThisClass Class<?> clazz, IRegistryHolder<? extends Block> block, AbstractBlockTintSource source) {
        AbstractForgeClientEventsImpl.BLOCK_COLOR_REGISTRY.listen(event -> event.getBlockColors().register((blockState, level, blockPos, index) -> source.calculate(blockState, level, blockPos, index), block.get()));
    }
}
