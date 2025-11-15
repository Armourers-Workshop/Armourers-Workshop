package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import org.jetbrains.annotations.Nullable;

public class BakedBackpackPartTransform implements ITransform {

    private SkinAttachmentPose attachmentPose;

    public void setup(@Nullable EntityRenderState renderState) {
        if (renderState != null && renderState != MannequinRenderState.getPlaceholder()) {
            attachmentPose = renderState.getAttachmentPose(SkinAttachmentTypes.BACKPACK, 0);
        } else {
            attachmentPose = null;
        }
    }

    @Override
    public void apply(IPoseStack poseStack) {
        if (attachmentPose != null) {
            poseStack.last().set(attachmentPose);
            poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
        }
    }
}
