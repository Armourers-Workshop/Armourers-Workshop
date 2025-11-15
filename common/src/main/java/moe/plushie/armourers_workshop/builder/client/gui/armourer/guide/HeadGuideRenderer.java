package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ModelPartElement;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;

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

    public void render(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(head, lightmap, overlay, SkinRenderType.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_HAT)) {
            context.draw(ModelPartElement.newInstance(hat, lightmap, overlay, SkinRenderType.PLAYER_CUTOUT_NO_CULL));
        }
    }
}
