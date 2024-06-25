package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class AllayBodyJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        var rootModelPart = model.getPart("root");
        var bodyModelPart = model.getPart("body");
        if (rootModelPart == null || bodyModelPart == null) {
            return transform;
        }
        var rootPose = rootModelPart.pose();
        var bodyPose = bodyModelPart.pose();
        return poseStack -> {
            rootPose.transform(poseStack);
            bodyPose.transform(poseStack);
            transform.apply(poseStack);
            poseStack.scale(0.5f, 0.5f, 0.5f);
        };
    }
}
