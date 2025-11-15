package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class DefaultSkirtJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        // ...
        var body = context.poses().byPartName("body");
        var leg1 = context.poses().byPartName("left_leg");
        var leg2 = context.poses().byPartName("right_leg");
        // sorry, but we can't complete this convert.
        if (body == null || leg1 == null || leg2 == null) {
            return transform;
        }
        return poseStack -> {
            var z = (leg1.z() + leg2.z()) / 2;
            poseStack.translate(body.x(), leg1.y(), z);
            if (body.yRot() != 0) {
                poseStack.rotate(OpenVector3f.YP.rotation(body.yRot()));
            }
            var xRot = (ort(leg1.xRot()) + ort(leg2.xRot())) / 2;
            if (Float.compare(xRot, 0) != 0) {
                poseStack.rotate(OpenVector3f.XP.rotation(xRot));
            }
            // yep, we intentionally discard part binding result.
            // because, correct binding need to calculate from multiple parts.
        };
    }

    private float ort(float q) {
        var pi = (float) Math.PI;
        if (q > pi) {
            return q - pi * 2;
        }
        if (q < -pi) {
            return q + pi * 2;
        }
        return q;
    }
}
