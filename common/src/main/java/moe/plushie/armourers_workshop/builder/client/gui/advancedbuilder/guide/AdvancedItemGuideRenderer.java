package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.skin.part.features.ICanHeld;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ModelPartElement;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;

public class AdvancedItemGuideRenderer extends AdvancedAbstractGuideRenderer {

    private final OpenModelPart armSolid;
    private final OpenModelPart armTransparent;

    public AdvancedItemGuideRenderer() {
        armSolid = OpenModelPartBuilder.player().uv(40, 16).cube(-2, -10, -4, 4, 8, 4).offset(0, 0, 0).build();
        armTransparent = OpenModelPartBuilder.player().uv(40, 24).cube(-2, -2, -4, 4, 4, 4).offset(0, 0, 0).build();
    }

    @Override
    public void render(SkinDocument document, int lightmap, int overlay, IGraphicsContext context) {
        var node = findItemNode(document.root());
        if (node == null) {
            return;
        }
        context.saveGraphicsState();

        applyOffset(context);

        context.draw(ModelPartElement.newInstance(armSolid, lightmap, overlay, 0xffffffff, SkinRenderType.PLAYER_CUTOUT));
        context.draw(ModelPartElement.newInstance(armTransparent, lightmap, overlay, 0xbfffffff, SkinRenderType.PLAYER_TRANSLUCENT));

        context.restoreGraphicsState();
    }

    protected void applyOffset(IGraphicsContext context) {
        context.translateCTM(0, 2, 0);
        context.rotateCTM(OpenVector3f.XP.rotationDegrees(-90));
        context.scaleCTM(16, 16, 16);
    }

    protected SkinDocumentNode findItemNode(SkinDocumentNode node) {
        // find first item node.
        if (node.type() instanceof ICanHeld) {
            return node;
        }
        for (var child : node.children()) {
            var result = findItemNode(child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
