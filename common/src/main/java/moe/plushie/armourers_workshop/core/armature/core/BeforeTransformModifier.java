package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class BeforeTransformModifier extends AfterTransformModifier {

    public BeforeTransformModifier(Vector3f translate, Vector3f scale, Vector3f rotate) {
        super(translate, scale, rotate);
    }

    @Override
    public ITransformf apply(ITransformf transform, IModelHolder<?> model) {
        ITransformf transform1 = super.apply(ITransformf.NONE, model);
        if (transform1 == ITransformf.NONE) {
            return transform;
        }
        return poseStack -> {
            transform1.apply(poseStack);
            transform.apply(poseStack);
        };
    }
}
