package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ModelPartElement;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;

public class FeetGuideRenderer extends AbstractGuideRenderer {

    protected final OpenModelPart legLeft;
    protected final OpenModelPart legRight;
    protected final OpenModelPart leftPants;
    protected final OpenModelPart rightPants;

    public FeetGuideRenderer() {
        legLeft = OpenModelPartBuilder.player().uv(16, 48).cube(-2, -12, -2, 4, 12, 4).build();
        legRight = OpenModelPartBuilder.player().uv(0, 16).cube(-2, -12, -2, 4, 12, 4).build();
        leftPants = OpenModelPartBuilder.player().uv(0, 48).cube(-2, -12, -2, 4, 12, 4, 0.25f).build();
        rightPants = OpenModelPartBuilder.player().uv(0, 32).cube(-2, -12, -2, 4, 12, 4, 0.25f).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_SKIRT, this::render);
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_THIGH, this::renderLeftLeg);
        rendererManager.register(SkinPartTypes.BIPPED_RIGHT_THIGH, this::renderRightLeg);
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_FOOT, this::renderLeftLeg);
        rendererManager.register(SkinPartTypes.BIPPED_RIGHT_FOOT, this::renderRightLeg);
    }

    public void render(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        float f = 1 / 16f;
        context.saveGraphicsState();
        context.translateCTM(2 * f, 0, 0);
        renderLeftLeg(provider, lightmap, overlay, context);
        context.translateCTM(-4 * f, 0, 0);
        renderRightLeg(provider, lightmap, overlay, context);
        context.restoreGraphicsState();
    }

    public void renderLeftLeg(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(legLeft, lightmap, overlay, SkinRenderType.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS)) {
            context.draw(ModelPartElement.newInstance(leftPants, lightmap, overlay, SkinRenderType.PLAYER_CUTOUT_NO_CULL));
        }
    }

    public void renderRightLeg(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(ModelPartElement.newInstance(legRight, lightmap, overlay, SkinRenderType.PLAYER_CUTOUT));
        if (provider.shouldRenderOverlay(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS)) {
            context.draw(ModelPartElement.newInstance(rightPants, lightmap, overlay, SkinRenderType.PLAYER_CUTOUT_NO_CULL));
        }
    }
}
