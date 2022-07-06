package moe.plushie.armourers_workshop.builder.render.guide;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.client.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.IGuideRenderer;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadGuideRenderer implements IGuideRenderer {

    protected final ModelRenderer head;
    protected final ModelRenderer hat;

    public HeadGuideRenderer() {
        head = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 0);
        head.addBox(-4, -8, -4, 8, 8, 8);

        hat = new ModelRenderer(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 32, 0);
        hat.addBox(-4, -8, -4, 8, 8, 8, 0.5f);
    }

    public static HeadGuideRenderer getInstance() {
        return new HeadGuideRenderer();
    }

    @Override
    public void render(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers) {
        head.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            hat.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
