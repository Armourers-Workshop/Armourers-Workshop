package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinModelManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.utils.ext.OpenModelPart;
import moe.plushie.armourers_workshop.utils.ext.OpenModelPartBuilder;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class AdvancedItemGuideRenderer extends AbstractAdvancedGuideRenderer {

    private final OpenModelPart armSolid;
    private final OpenModelPart armTransparent;

    public AdvancedItemGuideRenderer() {
        armSolid = OpenModelPartBuilder.player().uv(40, 16).cube(-2, -10, -4, 4, 8, 4).offset(0, 0, 0).build();
        armTransparent = OpenModelPartBuilder.player().uv(40, 24).cube(-2, -2, -4, 4, 4, 4).offset(0, 0, 0).build();
    }

    @Override
    public void render(SkinDocument document, IPoseStack poseStack, int light, int overlay, IBufferSource bufferSource) {
        SkinDocumentNode node = findItemNode(document.getRoot());
        if (node == null) {
            return;
        }
        poseStack.pushPose();

        applyTransform(poseStack, node, document.getItemTransforms());
        applyOffset(poseStack);

        renderModel(poseStack, light, overlay, bufferSource);

        poseStack.popPose();
    }

    protected void renderModel(IPoseStack poseStack, int light, int overlay, IBufferSource bufferSource) {
        armSolid.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), 0xf000f0, overlay);
        armTransparent.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_CUTOUT), 0xf000f0, overlay);
        //poseStack.translate(0, -0.001f * f, 0);
        //armTransparent.render(poseStack, bufferSource.getBuffer(SkinRenderType.PLAYER_TRANSLUCENT), 0xf000f0, overlay, 1, 1, 1, 0.75f);
    }

    protected void applyTransform(IPoseStack poseStack, SkinDocumentNode node, SkinItemTransforms itemTransforms) {
        if (itemTransforms != null) {
            ITransformf itemTransform = itemTransforms.get(AbstractItemTransformType.THIRD_PERSON_RIGHT_HAND);
            if (itemTransform != null) {
                itemTransform.apply(poseStack);
            }
        } else {
            auto entity = PlaceholderManager.MANNEQUIN.get();
            auto model = SkinModelManager.getInstance().getModel(node.getType(), null, ItemStack.EMPTY, entity);
            float f1 = 16f;
            float f2 = 1 / 16f;
            poseStack.scale(f1, f1, f1);
            model.applyTransform(poseStack, false, AbstractItemTransformType.THIRD_PERSON_LEFT_HAND);
            poseStack.scale(f2, f2, f2);
        }
    }

    protected void applyOffset(IPoseStack poseStack) {
        poseStack.translate(0, 0, -2);
        poseStack.scale(16, 16, 16);
        poseStack.rotate(Vector3f.XP.rotationDegrees(-90));
    }

    protected SkinDocumentNode findItemNode(SkinDocumentNode node) {
        // find first item node.
        if (node.getType() instanceof ICanHeld) {
            return node;
        }
        for (SkinDocumentNode child : node.children()) {
            SkinDocumentNode result = findItemNode(child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
