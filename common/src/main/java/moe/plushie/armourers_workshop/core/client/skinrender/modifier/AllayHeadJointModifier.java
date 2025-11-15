package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class AllayHeadJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        var root = context.poses().byPartName("root");
        if (root == null) {
            return transform;
        }
        return poseStack -> {
            root.transform(poseStack);
            transform.apply(poseStack);
            poseStack.scale(0.625f, 0.625f, 0.625f);
        };
    }
}
