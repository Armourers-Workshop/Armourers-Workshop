package moe.plushie.armourers_workshop.builder.render.guide;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.client.render.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.render.IGuideRenderer;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;

public class HeldItemGuideRenderer implements IGuideRenderer {

    private final static HeldItemGuideRenderer INSTANCE = new HeldItemGuideRenderer();

    private ModelRenderer armSolid;
    private ModelRenderer armTransparent;

    public HeldItemGuideRenderer() {
        armSolid = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 40, 16);
        armSolid.addBox(-2, -10, -4, 4, 8, 4);
        armSolid.setPos(0, 0, 0);

        armTransparent = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 40, 24);
        armTransparent.addBox(-2, -2, -4, 4, 4, 4);
        armTransparent.setPos(0, 0, 0);
    }

    public static HeldItemGuideRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        float f = 1 / 16f;
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
        armSolid.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        matrixStack.translate(0, -0.001 * f, 0);
        armTransparent.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_TRANSLUCENT), light, overlay, 1, 1, 1, 0.75f);
        matrixStack.popPose();
    }
}
