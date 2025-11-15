package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractBlockSpecialRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractItemSpecialRenderer;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AbstractForgeClientRegistry extends AbstractForgeClientRegistryImpl {

    @Override
    protected void registerItemTintSource(TypedHolder<? extends Item> item, AbstractItemTintSource source) {
        GameRenderer.registerItemTintSourceFO(item, source);
    }

    @Override
    protected void registerBlockTintSource(TypedHolder<? extends Block> block, AbstractBlockTintSource source) {
        GameRenderer.registerBlockTintSourceFO(block, source);
    }

    @Override
    protected void registerItemSpecialRenderer(TypedHolder<? extends Item> item, AbstractItemSpecialRenderer renderer) {
        GameRenderer.registerItemSpecialRendererFO(item, renderer);
    }

    @Override
    protected void registerBlockSpecialRenderer(TypedHolder<? extends Block> block, AbstractBlockSpecialRenderer renderer) {
        GameRenderer.registerBlockSpecialRendererFO(block, renderer);
    }
}
