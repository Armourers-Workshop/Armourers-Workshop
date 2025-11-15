package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ModelPartElement;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;

public class HeldItemGuideRenderer extends AbstractGuideRenderer {

    private final OpenModelPart armSolid;
    private final OpenModelPart armTransparent;

    public HeldItemGuideRenderer() {
        armSolid = OpenModelPartBuilder.player().uv(40, 16).cube(-2, -10, -4, 4, 8, 4).offset(0, 0, 0).build();
        armTransparent = OpenModelPartBuilder.player().uv(40, 24).cube(-2, -2, -4, 4, 4, 4).offset(0, 0, 0).build();
    }

    @Override
    public void init(GuideRendererManager rendererManager) {
        rendererManager.register(SkinPartTypes.ITEM_AXE, this::render);
        rendererManager.register(SkinPartTypes.ITEM_HOE, this::render);
        rendererManager.register(SkinPartTypes.ITEM_PICKAXE, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SHOVEL, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SHIELD, this::render);
        rendererManager.register(SkinPartTypes.ITEM_SWORD, this::render);
        rendererManager.register(SkinPartTypes.ITEM_TRIDENT, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW0, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW1, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW2, this::render);
        rendererManager.register(SkinPartTypes.ITEM_BOW3, this::render);
        rendererManager.register(SkinPartTypes.ITEM, this::render);
    }

    public void render(GuideDataProvider provider, int lightmap, int overlay, IGraphicsContext context) {
        float f = 1 / 16f;
        context.saveGraphicsState();
        context.rotateCTM(OpenVector3f.XP.rotationDegrees(-90));
        context.draw(ModelPartElement.newInstance(armSolid, lightmap, overlay, SkinRenderType.PLAYER_CUTOUT));
        context.translateCTM(0, -0.001f * f, 0);
        context.draw(ModelPartElement.newInstance(armTransparent, lightmap, overlay, 0xbfffffff, SkinRenderType.PLAYER_TRANSLUCENT));
        context.restoreGraphicsState();
    }
}
