package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class AfterTransformModifier extends JointModifier {

    private final ITransform3f value;

    public AfterTransformModifier(ITransform3f transform) {
        this.value = transform;
    }

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        if (value.isIdentity()) {
            return transform;
        }
        return poseStack -> {
            transform.apply(poseStack);
            value.apply(poseStack);
        };
    }
}
