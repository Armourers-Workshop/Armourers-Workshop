package moe.plushie.armourers_workshop.builder.render.guide;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.client.render.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.render.IGuideRenderer;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class FeetGuideRenderer implements IGuideRenderer {

    private static final FeetGuideRenderer INSTANCE = new FeetGuideRenderer();

    protected final ModelRenderer legLeft;
    protected final ModelRenderer legRight;
    protected final ModelRenderer leftPants;
    protected final ModelRenderer rightPants;

    public FeetGuideRenderer() {
        legLeft = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 16, 48);
        legLeft.addBox(-2, -12, -2, 4, 12, 4);

        legRight = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 16);
        legRight.addBox(-2, -12, -2, 4, 12, 4);

        leftPants = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 48);
        leftPants.addBox(-2, -12, -2, 4, 12, 4, 0.25f);

        rightPants = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 32);
        rightPants.addBox(-2, -12, -2, 4, 12, 4, 0.25f);
    }

    public static FeetGuideRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        float f = 1 / 16f;
        matrixStack.pushPose();
        matrixStack.translate(2 * f, 0, 0);
        renderLeftLeg(matrixStack, provider, light, overlay, buffers);
        matrixStack.translate(-4 * f, 0, 0);
        renderRightLeg(matrixStack, provider, light, overlay, buffers);
        matrixStack.popPose();
    }

    public void renderLeftLeg(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        legLeft.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            leftPants.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightLeg(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        legRight.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            rightPants.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
