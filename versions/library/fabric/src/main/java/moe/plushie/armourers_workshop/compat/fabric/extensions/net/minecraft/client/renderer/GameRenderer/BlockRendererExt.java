package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.client.renderer.block.AbstractBlockSpecialRenderer;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Optional;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, 1.26)")
@Extension
public class BlockRendererExt {

    private static final Map<String, RenderType> LAYERS = Collections.immutableMap(it -> {
        it.put("solid", RenderType.solid());
        it.put("cutout", RenderType.cutout());
        it.put("cutout_mipped", RenderType.cutoutMipped());
        it.put("translucent", RenderType.translucent());
        it.put("tripwire", RenderType.tripwire());
    });

    public static void registerBlockSpecialRendererFA(@ThisClass Class<?> clazz, IRegistryHolder<? extends Block> block, AbstractBlockSpecialRenderer provider) {
        Optional.ofNullable(LAYERS.get(provider.renderType())).ifPresent(it -> {
            BlockRenderLayerMap.INSTANCE.putBlock(block.get(), it);
        });
    }
}

