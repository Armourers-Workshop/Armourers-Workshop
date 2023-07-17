package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;

public class AllayHeadJointModifier extends ArmatureModifier {

    @Override
    public ITransformf apply(IJoint joint, IModel model, ITransformf transform) {
        IModelPart modelPart = model.getPart("root");
        if (modelPart == null) {
            return transform;
        }
        IModelPartPose rootPose = modelPart.pose();
        return poseStack -> {
            rootPose.transform(poseStack);
            transform.apply(poseStack);
            poseStack.scale(0.625f, 0.625f, 0.625f);
        };
    }
}
