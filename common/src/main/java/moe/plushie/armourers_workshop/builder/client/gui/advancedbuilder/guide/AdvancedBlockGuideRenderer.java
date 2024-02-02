package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class AdvancedBlockGuideRenderer extends AbstractAdvancedGuideRenderer {

    private final BlockState blockState;
    private final BlockRenderDispatcher blockRenderer;

    public AdvancedBlockGuideRenderer() {
        this.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(SkinDocument document, PoseStack poseStack, int light, int overlay, MultiBufferSource buffers) {
        poseStack.pushPose();
        poseStack.scale(-16, -16, 16);
        poseStack.translate(-0.5f, -1.5f, -0.5f);
        blockRenderer.renderSingleBlock(blockState, poseStack, buffers, 0xf000f0, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
