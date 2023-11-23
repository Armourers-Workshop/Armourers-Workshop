package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;

public class DefaultBabyJointModifier extends ArmatureModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        return poseStack -> {
            transform.apply(poseStack);
            IModelBabyPose babyPose = model.getBabyPose();
            if (babyPose == null) {
                return;
            }
            float scale = babyPose.getHeadScale();
            IVector3f offset = babyPose.getHeadOffset();
            poseStack.scale(scale, scale, scale);
            poseStack.translate(offset.getX() / 16f, offset.getY() / 16f, offset.getZ() / 16f);
        };
    }
}
