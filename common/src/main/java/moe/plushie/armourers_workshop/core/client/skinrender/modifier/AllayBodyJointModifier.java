package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class AllayBodyJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        var root = context.poses().byPartName("root");
        var body = context.poses().byPartName("body");
        if (root == null || body == null) {
            return transform;
        }
        return poseStack -> {
            root.transform(poseStack);
            body.transform(poseStack);
            transform.apply(poseStack);
            poseStack.scale(0.5f, 0.5f, 0.5f);
        };
    }
}
