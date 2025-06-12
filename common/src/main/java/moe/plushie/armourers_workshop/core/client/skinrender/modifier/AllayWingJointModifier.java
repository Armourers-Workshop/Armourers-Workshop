package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class AllayWingJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        var rootModelPart = model.partByName("root");
        var bodyModelPart = model.partByName("body");
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
            var yRot = wingPose.yRot();
            if (yRot != 0) {
                poseStack.rotate(OpenVector3f.YP.rotation(yRot));
            }
            poseStack.scale(0.5f, 0.5f, 0.5f);
        };
    }

    private IModelPart getWingPart(IJoint joint, IModel model) {
        if (joint.name().equals("Phalanx_R")) {
            return model.partByName("right_wing");
        }
        return model.partByName("left_wing");
    }
}
