package moe.plushie.armourers_workshop.builder.client.render.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class HeadGuideRenderer extends AbstractGuideRenderer {

    protected final ModelPart head;
    protected final ModelPart hat;

    public HeadGuideRenderer() {
        head = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 0, 0);
        head.addBox(-4, -8, -4, 8, 8, 8);

        hat = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 32, 0);
        hat.addBox(-4, -8, -4, 8, 8, 8, 0.5f);
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPED_HEAD, this::render);
    }

    public void render(PoseStack matrixStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        head.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            hat.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
