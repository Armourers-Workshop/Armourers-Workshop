package moe.plushie.armourers_workshop.core.skin.transform;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.math.Quaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class SkinFixedTransform extends SkinTransform {

    public Vector3f translatePre = Vector3f.ZERO;

    public Vector3f rotation = Vector3f.ZERO;
    public Vector3f translate = Vector3f.ZERO;
    public Vector3f scale = Vector3f.ONE;

    @Override
    public void pre(IPoseStack poseStack) {
        if (translatePre != Vector3f.ZERO) {
            poseStack.translate(translatePre.getX(), translatePre.getY(), translatePre.getZ());
        }
    }

    @Override
    public void post(IPoseStack poseStack) {
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        if (rotation != Vector3f.ZERO) {
            poseStack.rotate(new Quaternionf(rotation.getX(), rotation.getY(), rotation.getZ(), true));
        }
        if (scale != Vector3f.ONE) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
    }
}
