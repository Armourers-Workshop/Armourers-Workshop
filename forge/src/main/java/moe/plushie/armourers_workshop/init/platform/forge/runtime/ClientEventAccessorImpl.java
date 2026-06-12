package moe.plushie.armourers_workshop.init.platform.forge.runtime;

import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSource;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSource;
import moe.plushie.armourers_workshop.compat.client.renderer.block.AbstractBlockSpecialRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.item.AbstractItemSpecialRenderer;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientEventAccessor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ClientEventAccessorImpl implements ClientEventAccessor {

    @Override
    public void registerItemTintSource(TypedHolder<? extends Item> item, AbstractItemTintSource source) {
        GameRenderer.registerItemTintSourceFO(item, source);
    }

    @Override
    public void registerBlockTintSource(TypedHolder<? extends Block> block, AbstractBlockTintSource source) {
        GameRenderer.registerBlockTintSourceFO(block, source);
    }

    @Override
    public void registerItemSpecialRenderer(TypedHolder<? extends Item> item, AbstractItemSpecialRenderer renderer) {
        GameRenderer.registerItemSpecialRendererFO(item, renderer);
    }

    @Override
    public void registerBlockSpecialRenderer(TypedHolder<? extends Block> block, AbstractBlockSpecialRenderer renderer) {
        GameRenderer.registerBlockSpecialRendererFO(block, renderer);
    }
}
