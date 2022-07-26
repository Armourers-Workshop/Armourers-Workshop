package moe.plushie.armourers_workshop.builder.client.render.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
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
        rendererManager.register(SkinPartTypes.BIPED_LEFT_WING, this::render);
        // rendererManager.register(SkinPartTypes.BIPED_RIGHT_WING, this::render); // same to left wing
    }


    public void render(PoseStack matrixStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        float f = 1 / 16f;
        matrixStack.pushPose();
        matrixStack.translate(0, 0, -2 * f);
        chestGuideRenderer.render(matrixStack, provider, light, overlay, buffers);
        matrixStack.popPose();
    }
}
