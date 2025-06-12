package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class DefaultSkirtJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        // ...
        var body = model.partByName("body");
        var leg1 = model.partByName("left_leg");
        var leg2 = model.partByName("right_leg");
        // sorry, but we can't complete this convert.
        if (body == null || leg1 == null || leg2 == null) {
            return transform;
        }
        return poseStack -> {
            var z = (leg1.pose().z() + leg2.pose().z()) / 2;
            poseStack.translate(body.pose().x(), leg1.pose().y(), z);
            if (body.pose().yRot() != 0) {
                poseStack.rotate(OpenVector3f.YP.rotation(body.pose().yRot()));
            }
            var xRot = (ort(leg1.pose().xRot()) + ort(leg2.pose().xRot())) / 2;
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
