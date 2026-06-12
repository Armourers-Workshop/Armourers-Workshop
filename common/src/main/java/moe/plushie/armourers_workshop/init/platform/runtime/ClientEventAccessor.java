package moe.plushie.armourers_workshop.init.platform.runtime;

import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import moe.plushie.armourers_workshop.compat.client.renderer.block.AbstractBlockSpecialRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.item.AbstractItemSpecialRenderer;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface ClientEventAccessor {

    void registerItemTintSource(TypedHolder<? extends Item> item, AbstractItemTintSource source);

    void registerBlockTintSource(TypedHolder<? extends Block> block, AbstractBlockTintSource source);

    void registerItemSpecialRenderer(TypedHolder<? extends Item> item, AbstractItemSpecialRenderer renderer);

    void registerBlockSpecialRenderer(TypedHolder<? extends Block> block, AbstractBlockSpecialRenderer renderer);
}
