package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class DefaultSkirtJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        // ...
        var body = model.getPart("body");
        var leg1 = model.getPart("left_leg");
        var leg2 = model.getPart("right_leg");
        // sorry, but we can't complete this convert.
        if (body == null || leg1 == null || leg2 == null) {
            return transform;
        }
        return poseStack -> {
            var z = (leg1.pose().getZ() + leg2.pose().getZ()) / 2;
            poseStack.translate(body.pose().getX(), leg1.pose().getY(), z);
            if (body.pose().getYRot() != 0) {
                poseStack.rotate(Vector3f.YP.rotation(body.pose().getYRot()));
            }
            var xRot = (ort(leg1.pose().getXRot()) + ort(leg2.pose().getXRot())) / 2;
            if (Float.compare(xRot, 0) != 0) {
                poseStack.rotate(Vector3f.XP.rotation(xRot));
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
