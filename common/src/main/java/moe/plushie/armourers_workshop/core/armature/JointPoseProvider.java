package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightTransformProvider;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

public class JointPoseProvider {

    private final IEntityModel<?> entityModel;

    public JointPoseProvider(ArmatureTransformerContext context) {
        this.entityModel = context.entityModel();
    }

    public OpenPoseStack.Pose byName(String name) {
        return EpicFlightTransformProvider.of(entityModel).byName(name);
    }

    public IModelPartPose byPartName(String name) {
        var part = entityModel.abi$getPartByName(name);
        if (part != null) {
            return part.pose();
        }
        return null;
    }
}
