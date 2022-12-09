package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ModelPartBuilder;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public class HeldItemGuideRenderer extends AbstractGuideRenderer {

    private final ModelPart armSolid;
    private final ModelPart armTransparent;

    public HeldItemGuideRenderer() {
        armSolid = ModelPartBuilder.player().uv(40, 16).cube(-2, -10, -4, 4, 8, 4).offset(0, 0, 0).build();
        armTransparent = ModelPartBuilder.player().uv(40, 24).cube(-2, -2, -4, 4, 4, 4).offset(0, 0, 0).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.TOOL_AXE, this::render);
        rendererManager.register(SkinPartTypes.TOOL_HOE, this::render);
        rendererManager.register(SkinPartTypes.TOOL_PICKAXE, this::render);
        rendererManager.register(SkinPartTypes.TOOL_SHOVEL, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SHIELD, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SWORD, this::render);
        rendererManager.register(SkinPartTypes.ITEM_TRIDENT, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW1, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW2, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW3, this::render);
        rendererManager.register(SkinPartTypes.ITEM, this::render);
    }

    public void render(IPoseStack matrixStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        float f = 1 / 16f;
        matrixStack.pushPose();
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-90));
        armSolid.render(matrixStack.cast(), buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        matrixStack.translate(0, -0.001f * f, 0);
        armTransparent.render(matrixStack.cast(), buffers.getBuffer(SkinRenderType.PLAYER_TRANSLUCENT), light, overlay, 1, 1, 1, 0.75f);
        matrixStack.popPose();
    }
}
