package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ModelPartBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(EnvType.CLIENT)
public class HeadGuideRenderer extends AbstractGuideRenderer {

    protected final ModelPart head;
    protected final ModelPart hat;

    public HeadGuideRenderer() {
        head = ModelPartBuilder.player().uv(0, 0).cube(-4, -8, -4, 8, 8, 8).build();
        hat = ModelPartBuilder.player().uv(32, 0).cube(-4, -8, -4, 8, 8, 8, 0.5f).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_HEAD, this::render);
    }

    public void render(PoseStack poseStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource buffers) {
        head.render(poseStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay()) {
            hat.render(poseStack, buffers.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
