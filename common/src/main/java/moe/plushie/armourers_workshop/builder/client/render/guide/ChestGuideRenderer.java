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
public class ChestGuideRenderer extends AbstractGuideRenderer {

    protected final ModelPart body;
    protected final ModelPart leftArm;
    protected final ModelPart rightArm;
    protected final ModelPart jacket;
    protected final ModelPart leftSleeve;
    protected final ModelPart rightSleeve;

    public ChestGuideRenderer() {
        body = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 16, 16);
        body.addBox(-4, -12, -2, 8, 12, 4);

        leftArm = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 32, 48);
        leftArm.addBox(-1, -12, -2, 4, 12, 4);

        rightArm = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 40, 16);
        rightArm.addBox(-3, -12, -2, 4, 12, 4);

        leftSleeve = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 48, 48);
        leftSleeve.addBox(-1, -12, -2, 4, 12, 4, 0.25f);

        rightSleeve = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 40, 32);
        rightSleeve.addBox(-3, -12, -2, 4, 12, 4, 0.25f);

        jacket = new ModelPart(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, 16, 32);
        jacket.addBox(-4, -12, -2, 8, 12, 4, 0.25f);
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPED_CHEST, this::render);
        rendererManager.register(SkinPartTypes.BIPED_LEFT_ARM, this::renderLeftArm);
        rendererManager.register(SkinPartTypes.BIPED_RIGHT_ARM, this::renderRightArm);
    }

    public void render(PoseStack matrixStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        body.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            jacket.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderLeftArm(PoseStack matrixStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        leftArm.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            leftSleeve.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }

    public void renderRightArm(PoseStack matrixStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        rightArm.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            rightSleeve.render(matrixStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
