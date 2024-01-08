package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class AdvancedHorseGuideRenderer extends AbstractAdvancedGuideRenderer {

    public AdvancedHorseGuideRenderer() {
    }

    @Override
    public void render(PoseStack poseStack, int light, int overlay, float r, float g, float b, float alpha, MultiBufferSource buffers) {
        auto entity = PlaceholderManager.HORSE.get();
        auto rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        poseStack.pushPose();
        poseStack.scale(-16, -16, 16);
        poseStack.translate(0.0f, -0.0f, 0.0f);

        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, poseStack, buffers, 0xf000f0));

        poseStack.popPose();
    }
}
