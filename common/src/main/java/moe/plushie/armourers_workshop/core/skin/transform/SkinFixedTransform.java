package moe.plushie.armourers_workshop.core.skin.transform;

import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class SkinFixedTransform extends SkinTransform {

    public Vector3f translatePre = Vector3f.ZERO;

    public Vector3f rotation = Vector3f.ZERO;
    public Vector3f translate = Vector3f.ZERO;
    public Vector3f scale = Vector3f.ONE;

    @Override
    public void pre(OpenPoseStack poseStack) {
        if (translatePre != Vector3f.ZERO) {
            poseStack.translate(translatePre.getX(), translatePre.getY(), translatePre.getZ());
        }
    }

    @Override
    public void post(OpenPoseStack poseStack) {
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        if (rotation != Vector3f.ZERO) {
            poseStack.mul(TrigUtils.rotate(rotation.getX(), rotation.getY(), rotation.getZ(), true));
        }
        if (scale != Vector3f.ONE) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
    }
}
