package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;

public class DefaultSkirtJointModifier extends ArmatureModifier {

    @Override
    public ITransformf apply(ITransformf transform, IModelHolder<?> model) {
        // ...
        ModelPart body = model.getPart("body");
        ModelPart leg1 = model.getPart("left_leg");
        ModelPart leg2 = model.getPart("right_leg");
        // sorry, but we can't complete this convert.
        if (body == null || leg1 == null || leg2 == null) {
            return transform;
        }
        return poseStack -> {
            float z = (leg1.z + leg2.z) / 2;
            poseStack.translate(body.x, leg1.y, z);
            if (body.yRot != 0) {
                poseStack.mulPose(Vector3f.YP.rotation(body.yRot));
            }
            float xRot = (ort(leg1.xRot) + ort(leg2.xRot)) / 2;
            if (Float.compare(xRot, 0) != 0) {
                poseStack.mulPose(Vector3f.XP.rotation(xRot));
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
