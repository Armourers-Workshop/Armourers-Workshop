package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.armature.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;

public class DefaultJointBinder extends ArmatureModifier {

    private final String name;

    public DefaultJointBinder(String name) {
        this.name = name;
    }

    @Override
    public ITransformf apply(IJoint joint, IModel model, ITransformf transform) {
        IModelPart modelPart = model.getPart(name);
        if (modelPart == null) {
            return transform;
        }
        IModelPartPose pose = modelPart.pose();
        return poseStack -> {
            transform.apply(poseStack);
            pose.transform(poseStack);
        };
    }
}
