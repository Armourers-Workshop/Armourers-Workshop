package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class AllayWingJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        var rootModelPart = model.getPart("root");
        var bodyModelPart = model.getPart("body");
        var wingModelPart = getWingPart(joint, model);
        if (rootModelPart == null || bodyModelPart == null || wingModelPart == null) {
            return transform;
        }
        var rootPose = rootModelPart.pose();
        var bodyPose = bodyModelPart.pose();
        var wingPose = wingModelPart.pose();
        return poseStack -> {
            rootPose.transform(poseStack);
            bodyPose.transform(poseStack);
            transform.apply(poseStack);
            var yRot = wingPose.getYRot();
            if (yRot != 0) {
                poseStack.rotate(Vector3f.YP.rotation(yRot));
            }
            poseStack.scale(0.5f, 0.5f, 0.5f);
        };
    }

    private IModelPart getWingPart(IJoint joint, IModel model) {
        if (joint.getName().equals("Phalanx_R")) {
            return model.getPart("right_wing");
        }
        return model.getPart("left_wing");
    }
}
