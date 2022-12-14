package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ModelPartBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class FeetGuideRenderer extends AbstractGuideRenderer {

    protected final ModelPart legLeft;
    protected final ModelPart legRight;
    protected final ModelPart leftPants;
    protected final ModelPart rightPants;

    public FeetGuideRenderer() {
        legLeft = ModelPartBuilder.player().uv(16, 48).cube(-2, -12, -2, 4, 12, 4).build();
        legRight = ModelPartBuilder.player().uv(0, 16).cube(-2, -12, -2, 4, 12, 4).build();
        leftPants = ModelPartBuilder.player().uv(0, 48).cube(-2, -12, -2, 4, 12, 4, 0.25f).build();
        rightPants = ModelPartBuilder.player().uv(0, 32).cube(-2, -12, -2, 4, 12, 4, 0.25f).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPED_SKIRT, this::render);
        rendererManager.register(SkinPartTypes.BIPED_LEFT_LEG, this::renderLeftLeg);
        rendererManager.register(SkinPartTypes.BIPED_RIGHT_LEG, this::renderRightLeg);
        rendererManager.register(SkinPartTypes.BIPED_LEFT_FOOT, this::renderLeftLeg);
        rendererManager.register(SkinPartTypes.BIPED_RIGHT_FOOT, this::renderRightLeg);
    }

    public void render(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        float f = 1 / 16f;
        poseStack.pushPose();
        poseStack.translate(2 * f, 0, 0);
        renderLeftLeg(poseStack, provider, light, overlay, buffers);
        poseStack.translate(-4 * f, 0, 0);
        renderRightLeg(poseStack, provider, light, overlay, buffers);
        poseStack.popPose();
    }

    public void renderLeftLeg(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        legLeft.render(poseStack.cast(), buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            leftPants.render(poseStack.cast(), buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightLeg(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        legRight.render(poseStack.cast(), buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            rightPants.render(poseStack.cast(), buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
