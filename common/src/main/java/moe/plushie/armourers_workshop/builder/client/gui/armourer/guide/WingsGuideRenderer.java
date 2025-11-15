package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;

@OnlyIn(Dist.CLIENT)
public class WingsGuideRenderer extends AbstractGuideRenderer {

    private final ChestGuideRenderer chestGuideRenderer = new ChestGuideRenderer();

    public static WingsGuideRenderer getInstance() {
        return new WingsGuideRenderer();
    }

    public WingsGuideRenderer() {
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.BIPPED_LEFT_WING, this::render);
        // rendererManager.register(SkinPartTypes.BIPPED_RIGHT_WING, this::render); // same to left wing
    }


    public void render(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        float f = 1 / 16f;
        context.saveGraphicsState();
        context.translateCTM(0, 0, -2 * f);
        chestGuideRenderer.render(provider, lightmap, overlay, context);
        context.restoreGraphicsState();
    }
}
