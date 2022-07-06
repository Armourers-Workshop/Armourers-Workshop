package moe.plushie.armourers_workshop.builder.render.guide;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.client.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.IGuideRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WingsGuideRenderer implements IGuideRenderer {

    private final ChestGuideRenderer chestGuideRenderer = ChestGuideRenderer.getInstance();

    public static WingsGuideRenderer getInstance() {
        return new WingsGuideRenderer();
    }

    @Override
    public void render(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        float f = 1 / 16f;
        matrixStack.pushPose();
        matrixStack.translate(0, 0, -2 * f);
        chestGuideRenderer.render(matrixStack, provider, light, overlay, buffers);
        matrixStack.popPose();
    }
}
