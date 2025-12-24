package moe.plushie.armourers_workshop.compat.fabric;

import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import moe.plushie.armourers_workshop.compat.client.renderer.block.AbstractBlockSpecialRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.item.AbstractItemSpecialRenderer;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AbstractFabricClientRegistry extends AbstractFabricClientRegistryImpl {

    @Override
    protected void registerItemTintSource(TypedHolder<? extends Item> item, AbstractItemTintSource source) {
        GameRenderer.registerItemTintSourceFA(item, source);
    }

    @Override
    protected void registerBlockTintSource(TypedHolder<? extends Block> block, AbstractBlockTintSource source) {
        GameRenderer.registerBlockTintSourceFA(block, source);
    }

    @Override
    protected void registerItemSpecialRenderer(TypedHolder<? extends Item> item, AbstractItemSpecialRenderer renderer) {
        GameRenderer.registerItemSpecialRendererFA(item, renderer);
    }

    @Override
    protected void registerBlockSpecialRenderer(TypedHolder<? extends Block> block, AbstractBlockSpecialRenderer renderer) {
        GameRenderer.registerBlockSpecialRendererFA(block, renderer);
    }
}
