package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class DefaultBabyJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        return poseStack -> {
            transform.apply(poseStack);
            var babyPose = model.babyPose();
            if (babyPose == null) {
                return;
            }
            var scale = babyPose.headScale();
            var offset = babyPose.headOffset();
            poseStack.scale(scale, scale, scale);
            poseStack.translate(offset.x() / 16f, offset.y() / 16f, offset.z() / 16f);
        };
    }
}
