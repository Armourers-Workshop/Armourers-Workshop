package moe.plushie.armourers_workshop.builder.client.render.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.IGuideRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class WingsGuideRenderer implements IGuideRenderer {

    private final ChestGuideRenderer chestGuideRenderer = ChestGuideRenderer.getInstance();

    public static WingsGuideRenderer getInstance() {
        return new WingsGuideRenderer();
    }

    @Override
    public void render(PoseStack matrixStack, IGuideProvider provider, int light, int overlay, MultiBufferSource buffers) {
        float f = 1 / 16f;
        matrixStack.pushPose();
        matrixStack.translate(0, 0, -2 * f);
        chestGuideRenderer.render(matrixStack, provider, light, overlay, buffers);
        matrixStack.popPose();
    }
}
