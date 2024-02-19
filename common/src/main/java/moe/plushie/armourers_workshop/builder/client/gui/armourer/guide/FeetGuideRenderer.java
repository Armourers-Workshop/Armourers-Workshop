package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.ext.OpenModelPart;
import moe.plushie.armourers_workshop.utils.ext.OpenModelPartBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FeetGuideRenderer extends AbstractGuideRenderer {

    protected final OpenModelPart legLeft;
    protected final OpenModelPart legRight;
    protected final OpenModelPart leftPants;
    protected final OpenModelPart rightPants;

    public FeetGuideRenderer() {
        legLeft = OpenModelPartBuilder.player().uv(16, 48).cube(-2, -12, -2, 4, 12, 4).build();
        legRight = OpenModelPartBuilder.player().uv(0, 16).cube(-2, -12, -2, 4, 12, 4).build();
        leftPants = OpenModelPartBuilder.player().uv(0, 48).cube(-2, -12, -2, 4, 12, 4, 0.25f).build();
        rightPants = OpenModelPartBuilder.player().uv(0, 32).cube(-2, -12, -2, 4, 12, 4, 0.25f).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_SKIRT, this::render);
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_THIGH, this::renderLeftLeg);
        rendererManager.register(SkinPartTypes.BIPPED_RIGHT_THIGH, this::renderRightLeg);
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_FOOT, this::renderLeftLeg);
        rendererManager.register(SkinPartTypes.BIPPED_RIGHT_FOOT, this::renderRightLeg);
    }

    public void render(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        float f = 1 / 16f;
        poseStack.pushPose();
        poseStack.translate(2 * f, 0, 0);
        renderLeftLeg(poseStack, provider, light, overlay, bufferSource);
        poseStack.translate(-4 * f, 0, 0);
        renderRightLeg(poseStack, provider, light, overlay, bufferSource);
        poseStack.popPose();
    }

    public void renderLeftLeg(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        legLeft.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS)) {
            leftPants.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightLeg(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        legRight.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS)) {
            rightPants.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
