package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.client.armature.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;

public class EpicFightJointBinder extends ArmatureModifier {

    private final String name;

    public EpicFightJointBinder(String name) {
        this.name = name;
    }

    @Override
    public ITransformf apply(IJoint joint, IModel model, ITransformf transform){
        return poseStack -> {
            ITransformf transform1 = model.getAssociatedObject(EpicFlightTransformProvider.KEY).apply(name);

            // the extra transforms is based on 1:1, but we own transforms is based on 1:16.
            // because we will zoom out the pose stack before the call apply,
            // so we must be manual fix when we use an extra transforms.
            float f1 = 16f;
            float f2 = 1 / 16f;
            poseStack.scale(f1, f1, f1);
            transform1.apply(poseStack);
            poseStack.scale(f2, f2, f2);

            transform.apply(poseStack);
        };
    }
}
