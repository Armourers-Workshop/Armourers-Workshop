package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.part.features.ICanHeld;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancedItemGuideRenderer extends AdvancedAbstractGuideRenderer {

    private final OpenModelPart armSolid;
    private final OpenModelPart armTransparent;

    public AdvancedItemGuideRenderer() {
        armSolid = OpenModelPartBuilder.player().uv(40, 16).cube(-2, -10, -4, 4, 8, 4).offset(0, 0, 0).build();
        armTransparent = OpenModelPartBuilder.player().uv(40, 24).cube(-2, -2, -4, 4, 4, 4).offset(0, 0, 0).build();
    }

    @Override
    public void render(SkinDocument document, IPoseStack poseStack, int light, int overlay, IBufferSource bufferSource) {
        SkinDocumentNode node = findItemNode(document.root());
        if (node == null) {
            return;
        }
        poseStack.pushPose();

        applyOffset(poseStack);

        var skinBufferSource = SkinVertexBufferBuilder.of(bufferSource);
        armSolid.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), 0xf000f0, overlay);
        //poseStack.translate(0, -0.001f * f, 0);
        armTransparent.render(poseStack, skinBufferSource.getBuffer(SkinRenderType.PLAYER_TRANSLUCENT), 0xf000f0, overlay, 0xbfffffff);

        poseStack.popPose();
    }

    protected void applyOffset(IPoseStack poseStack) {
        poseStack.translate(0, 2, 0);
        poseStack.rotate(OpenVector3f.XP.rotationDegrees(-90));
        poseStack.scale(16, 16, 16);
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
