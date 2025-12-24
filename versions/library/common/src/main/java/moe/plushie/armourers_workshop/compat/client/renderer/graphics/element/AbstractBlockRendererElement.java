package moe.plushie.armourers_workshop.compat.client.renderer.graphics.element;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderable;
import moe.plushie.armourers_workshop.core.client.render.element.SpecialRenderElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.state.BlockState;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractBlockRendererElement extends SpecialRenderElement implements AbstractGraphicsRenderable {

    private final BlockState blockState;
    private final int lightmap;
    private final int overlay;

    protected AbstractBlockRendererElement(BlockState blockState, int lightmap, int overlay) {
        this.blockState = blockState;
        this.lightmap = lightmap;
        this.overlay = overlay;
    }

    public static AbstractBlockRendererElement newInstance(BlockState blockState, int lightmap, int overlay) {
        return new AbstractBlockRendererElement(blockState, lightmap, overlay);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource, lightmap, overlay);
    }
}
