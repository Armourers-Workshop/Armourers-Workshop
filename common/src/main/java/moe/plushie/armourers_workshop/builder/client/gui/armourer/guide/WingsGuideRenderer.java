package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class WingsGuideRenderer extends AbstractGuideRenderer {

    private final ChestGuideRenderer chestGuideRenderer = new ChestGuideRenderer();

    public static WingsGuideRenderer getInstance() {
        return new WingsGuideRenderer();
    }

    public WingsGuideRenderer() {
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_WING, this::render);
        // rendererManager.register(SkinPartTypes.BIPPED_RIGHT_WING, this::render); // same to left wing
    }


    public void render(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        float f = 1 / 16f;
        poseStack.pushPose();
        poseStack.translate(0, 0, -2 * f);
        chestGuideRenderer.render(poseStack, provider, light, overlay, buffers);
        poseStack.popPose();
    }
}
