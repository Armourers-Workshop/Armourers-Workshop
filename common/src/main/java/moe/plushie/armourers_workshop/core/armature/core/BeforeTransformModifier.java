package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;

public class BeforeTransformModifier extends AfterTransformModifier {

    public BeforeTransformModifier(ITransform3f transform) {
        super(transform);
    }

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        var transform1 = super.apply(IJointTransform.NONE, joint, context);
        if (transform1 == IJointTransform.NONE) {
            return transform;
        }
        return poseStack -> {
            transform1.apply(poseStack);
            transform.apply(poseStack);
        };
    }
}
