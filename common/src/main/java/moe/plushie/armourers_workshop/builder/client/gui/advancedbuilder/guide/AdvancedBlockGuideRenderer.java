package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import com.mojang.blaze3d.vertex.PoseStack;
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

    private final BlockState blockState = Blocks.GRASS_BLOCK.defaultBlockState();

    private final BlockRenderDispatcher blockRenderDispatcher;


    public AdvancedBlockGuideRenderer() {
        this.blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(PoseStack poseStack, int light, int overlay, float r, float g, float b, float alpha, MultiBufferSource buffers) {
        poseStack.pushPose();
        poseStack.scale(-16, -16, 16);
        poseStack.translate(-0.5f, -1.5f, -0.5f);
        blockRenderDispatcher.renderSingleBlock(blockState, poseStack, buffers, 0xf000f0, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
