package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class AllayWingJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        var root = context.poses().byPartName("root");
        var body = context.poses().byPartName("body");
        var wings = getWingPart(joint, context);
        if (root == null || body == null || wings == null) {
            return transform;
        }
        return poseStack -> {
            root.transform(poseStack);
            body.transform(poseStack);
            transform.apply(poseStack);
            var yRot = wings.yRot();
            if (yRot != 0) {
                poseStack.rotate(OpenVector3f.YP.rotation(yRot));
            }
            poseStack.scale(0.5f, 0.5f, 0.5f);
        };
    }

    private IModelPartPose getWingPart(IJoint joint, JointContext context) {
        if (joint.name().equals("Phalanx_R")) {
            return context.poses().byPartName("right_wing");
        }
        return context.poses().byPartName("left_wing");
    }
}
