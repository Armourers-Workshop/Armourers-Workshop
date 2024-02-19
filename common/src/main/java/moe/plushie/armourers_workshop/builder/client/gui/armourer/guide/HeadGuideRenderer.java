package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.ext.OpenModelPart;
import moe.plushie.armourers_workshop.utils.ext.OpenModelPartBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HeadGuideRenderer extends AbstractGuideRenderer {

    protected final OpenModelPart head;
    protected final OpenModelPart hat;

    public HeadGuideRenderer() {
        head = OpenModelPartBuilder.player().uv(0, 0).cube(-4, -8, -4, 8, 8, 8).build();
        hat = OpenModelPartBuilder.player().uv(32, 0).cube(-4, -8, -4, 8, 8, 8, 0.5f).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_HEAD, this::render);
    }

    public void render(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, IBufferSource bufferSource) {
        head.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), light, overlay);
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_HAT)) {
            hat.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT_NO_CULL), light, overlay);
        }
    }
}
