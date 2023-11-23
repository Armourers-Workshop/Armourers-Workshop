package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class BeforeTransformModifier extends AfterTransformModifier {

    public BeforeTransformModifier(Vector3f translate, Vector3f scale, Vector3f rotate) {
        super(translate, scale, rotate);
    }

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        IJointTransform transform1 = super.apply(joint, model, IJointTransform.NONE);
        if (transform1 == IJointTransform.NONE) {
            return transform;
        }
        return poseStack -> {
            transform1.apply(poseStack);
            transform.apply(poseStack);
        };
    }
}
