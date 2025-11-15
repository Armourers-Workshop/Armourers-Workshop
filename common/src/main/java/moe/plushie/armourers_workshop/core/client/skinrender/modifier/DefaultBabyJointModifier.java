package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class DefaultBabyJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        var baby = context.poses().byPartName("baby");
        if (baby == null) {
            return transform;
        }
        return poseStack -> {
            transform.apply(poseStack);
            baby.transform(poseStack);
        };
    }
}
