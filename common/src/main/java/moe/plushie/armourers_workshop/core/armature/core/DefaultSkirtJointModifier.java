package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class DefaultSkirtJointModifier extends ArmatureModifier {

    @Override
    public ITransformf apply(IJoint joint, IModel model, ITransformf transform) {
        // ...
        IModelPart body = model.getPart("body");
        IModelPart leg1 = model.getPart("left_leg");
        IModelPart leg2 = model.getPart("right_leg");
        // sorry, but we can't complete this convert.
        if (body == null || leg1 == null || leg2 == null) {
            return transform;
        }
        return poseStack -> {
            float z = (leg1.pose().getZ() + leg2.pose().getZ()) / 2;
            poseStack.translate(body.pose().getX(), leg1.pose().getY(), z);
            if (body.pose().getYRot() != 0) {
                poseStack.rotate(Vector3f.YP.rotation(body.pose().getYRot()));
            }
            float xRot = (ort(leg1.pose().getXRot()) + ort(leg2.pose().getXRot())) / 2;
            if (Float.compare(xRot, 0) != 0) {
                poseStack.rotate(Vector3f.XP.rotation(xRot));
            }
            // yep, we intentionally discard part binding result.
            // because, correct binding need to calculate from multiple parts.
        };
    }

    private float ort(float q) {
        float pi = (float) Math.PI;
        if (q > pi) {
            return q - pi * 2;
        }
        if (q < -pi) {
            return q + pi * 2;
        }
        return q;
    }
}
