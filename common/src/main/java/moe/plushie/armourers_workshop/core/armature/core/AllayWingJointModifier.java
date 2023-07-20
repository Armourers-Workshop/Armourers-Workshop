package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.armature.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;
import moe.plushie.armourers_workshop.core.armature.Joints;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class AllayWingJointModifier extends ArmatureModifier {

    @Override
    public ITransformf apply(IJoint joint, IModel model, ITransformf transform) {
        IModelPart rootModelPart = model.getPart("root");
        IModelPart bodyModelPart = model.getPart("body");
        IModelPart wingModelPart = getWingPart(joint, model);
        if (rootModelPart == null || bodyModelPart == null || wingModelPart == null) {
            return transform;
        }
        IModelPartPose rootPose = rootModelPart.pose();
        IModelPartPose bodyPose = bodyModelPart.pose();
        IModelPartPose wingPose = wingModelPart.pose();;
        return poseStack -> {
            rootPose.transform(poseStack);
            bodyPose.transform(poseStack);
            transform.apply(poseStack);
            float yRot = wingPose.getYRot();
            if (yRot != 0) {
                poseStack.rotate(Vector3f.YP.rotation(yRot));
            }
            poseStack.scale(0.5f, 0.5f, 0.5f);
        };
    }

    private IModelPart getWingPart(IJoint joint, IModel model) {
        if (joint == Joints.BIPPED_RIGHT_PHALANX) {
            return model.getPart("right_wing");
        }
        return model.getPart("left_wing");
    }
}
