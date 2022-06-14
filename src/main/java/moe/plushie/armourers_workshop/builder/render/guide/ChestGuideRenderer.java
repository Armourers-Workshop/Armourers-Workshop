package moe.plushie.armourers_workshop.builder.render.guide;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.client.render.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.render.IGuideRenderer;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestGuideRenderer implements IGuideRenderer {

    private static final ChestGuideRenderer INSTANCE = new ChestGuideRenderer();

    protected final ModelRenderer body;
    protected final ModelRenderer leftArm;
    protected final ModelRenderer rightArm;
    protected final ModelRenderer jacket;
    protected final ModelRenderer leftSleeve;
    protected final ModelRenderer rightSleeve;

    public ChestGuideRenderer() {
        body = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 16, 16);
        body.addBox(-4, -12, -2, 8, 12, 4);

        leftArm = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 32, 48);
        leftArm.addBox(-1, -12, -2, 4, 12, 4);

        rightArm = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 40, 16);
        rightArm.addBox(-3, -12, -2, 4, 12, 4);

        leftSleeve = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 48, 48);
        leftSleeve.addBox(-1, -12, -2, 4, 12, 4, 0.25f);

        rightSleeve = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 40, 32);
        rightSleeve.addBox(-3, -12, -2, 4, 12, 4, 0.25f);

        jacket = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 16, 32);
        jacket.addBox(-4, -12, -2, 8, 12, 4, 0.25f);
    }

    public static ChestGuideRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        body.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            jacket.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderLeftArm(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        leftArm.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            leftSleeve.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightArm(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        rightArm.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            rightSleeve.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
