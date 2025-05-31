package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChestGuideRenderer extends AbstractGuideRenderer {

    protected final OpenModelPart body;
    protected final OpenModelPart jacket;
    protected final OpenModelPart leftArm;
    protected final OpenModelPart rightArm;
    protected final OpenModelPart leftSleeve;
    protected final OpenModelPart rightSleeve;
    protected final OpenModelPart leftArmSlim;
    protected final OpenModelPart rightArmSlim;
    protected final OpenModelPart leftSleeveSlim;
    protected final OpenModelPart rightSleeveSlim;

    public ChestGuideRenderer() {
        body = OpenModelPartBuilder.player().uv(16, 16).cube(-4, -12, -2, 8, 12, 4).build();
        jacket = OpenModelPartBuilder.player().uv(16, 32).cube(-4, -12, -2, 8, 12, 4, 0.25f).build();
        leftArm = OpenModelPartBuilder.player().uv(32, 48).cube(-1, -12, -2, 4, 12, 4).build();
        rightArm = OpenModelPartBuilder.player().uv(40, 16).cube(-3, -12, -2, 4, 12, 4).build();
        leftSleeve = OpenModelPartBuilder.player().uv(48, 48).cube(-1, -12, -2, 4, 12, 4, 0.25f).build();
        rightSleeve = OpenModelPartBuilder.player().uv(40, 32).cube(-3, -12, -2, 4, 12, 4, 0.25f).build();
        // config the slim model part.
        leftArmSlim = OpenModelPartBuilder.player().uv(32, 48).cube(-1, -12, -2, 3, 12, 4).build();
        rightArmSlim = OpenModelPartBuilder.player().uv(40, 16).cube(-2, -12, -2, 3, 12, 4).build();
        leftSleeveSlim = OpenModelPartBuilder.player().uv(48, 48).cube(-1, -12, -2, 3, 12, 4, 0.25f).build();
        rightSleeveSlim = OpenModelPartBuilder.player().uv(40, 32).cube(-2, -12, -2, 3, 12, 4, 0.25f).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_CHEST, this::render);
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_ARM, this::renderLeftArm);
        rendererManager.register(SkinPartTypes.BIPPED_RIGHT_ARM, this::renderRightArm);
        rendererManager.register(EntityTextureDescriptor.Model.ALEX, SkinPartTypes.BIPPED_LEFT_ARM, this::renderLeftArmSlim);
        rendererManager.register(EntityTextureDescriptor.Model.ALEX, SkinPartTypes.BIPPED_RIGHT_ARM, this::renderRightArmSlim);
    }

    public void render(IPoseStack poseStack, GuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        body.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_JACKET)) {
            jacket.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderLeftArm(IPoseStack poseStack, GuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        leftArm.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE)) {
            leftSleeve.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightArm(IPoseStack poseStack, GuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        rightArm.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE)) {
            rightSleeve.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderLeftArmSlim(IPoseStack poseStack, GuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        leftArmSlim.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE)) {
            leftSleeveSlim.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightArmSlim(IPoseStack poseStack, GuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        rightArmSlim.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE)) {
            rightSleeveSlim.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
