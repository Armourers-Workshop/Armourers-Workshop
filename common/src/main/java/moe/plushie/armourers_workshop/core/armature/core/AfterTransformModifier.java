package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class AfterTransformModifier extends JointModifier {

    private final ITransformf value;

    public AfterTransformModifier(ITransformf transform) {
        this.value = transform;
    }

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        if (value.isIdentity()) {
            return transform;
        }
        return poseStack -> {
            transform.apply(poseStack);
            value.apply(poseStack);
        };
    }
}
