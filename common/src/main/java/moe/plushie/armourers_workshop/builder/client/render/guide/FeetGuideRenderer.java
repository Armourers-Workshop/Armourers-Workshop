package moe.plushie.armourers_workshop.builder.client.render.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.IGuideRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class FeetGuideRenderer implements IGuideRenderer {

    private static final FeetGuideRenderer INSTANCE = new FeetGuideRenderer();

    protected final ModelPart legLeft;
    protected final ModelPart legRight;
    protected final ModelPart leftPants;
    protected final ModelPart rightPants;

    public FeetGuideRenderer() {
        legLeft = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 16, 48);
        legLeft.addBox(-2, -12, -2, 4, 12, 4);

        legRight = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 16);
        legRight.addBox(-2, -12, -2, 4, 12, 4);

        leftPants = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 48);
        leftPants.addBox(-2, -12, -2, 4, 12, 4, 0.25f);

        rightPants = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 32);
        rightPants.addBox(-2, -12, -2, 4, 12, 4, 0.25f);
    }

    public static FeetGuideRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(PoseStack matrixStack, IGuideProvider provider, int light, int overlay, MultiBufferSource buffers) {
        float f = 1 / 16f;
        matrixStack.pushPose();
        matrixStack.translate(2 * f, 0, 0);
        renderLeftLeg(matrixStack, provider, light, overlay, buffers);
        matrixStack.translate(-4 * f, 0, 0);
        renderRightLeg(matrixStack, provider, light, overlay, buffers);
        matrixStack.popPose();
    }

    public void renderLeftLeg(PoseStack matrixStack, IGuideProvider provider, int light, int overlay, MultiBufferSource buffers) {
        legLeft.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            leftPants.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightLeg(PoseStack matrixStack, IGuideProvider provider, int light, int overlay, MultiBufferSource buffers) {
        legRight.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            rightPants.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
