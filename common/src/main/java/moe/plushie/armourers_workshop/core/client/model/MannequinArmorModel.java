package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractMannequinArmorModel;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MannequinArmorModel<T extends MannequinEntity> extends AbstractMannequinArmorModel<T> {

    public MannequinArmorModel(AbstractEntityRendererProvider.Context context, Type type) {
        super(context, type);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> normalInner(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.NORMAL_INNER);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> normalOuter(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.NORMAL_OUTER);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> slimInner(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.SLIM_INNER);
    }

    public static <T extends MannequinEntity> MannequinArmorModel<T> slimOuter(AbstractEntityRendererProvider.Context context) {
        return new MannequinArmorModel<>(context, Type.SLIM_OUTER);
    }

    @Override
    public void setupAnim(T entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        this.head.xRot = MathUtils.toRadians(entity.getHeadPose().getX());
        this.head.yRot = MathUtils.toRadians(entity.getHeadPose().getY());
        this.head.zRot = MathUtils.toRadians(entity.getHeadPose().getZ());
        this.leftArm.xRot = MathUtils.toRadians(entity.getLeftArmPose().getX());
        this.leftArm.yRot = MathUtils.toRadians(entity.getLeftArmPose().getY());
        this.leftArm.zRot = MathUtils.toRadians(entity.getLeftArmPose().getZ());
        this.rightArm.xRot = MathUtils.toRadians(entity.getRightArmPose().getX());
        this.rightArm.yRot = MathUtils.toRadians(entity.getRightArmPose().getY());
        this.rightArm.zRot = MathUtils.toRadians(entity.getRightArmPose().getZ());
        this.leftLeg.xRot = MathUtils.toRadians(entity.getLeftLegPose().getX());
        this.leftLeg.yRot = MathUtils.toRadians(entity.getLeftLegPose().getY());
        this.leftLeg.zRot = MathUtils.toRadians(entity.getLeftLegPose().getZ());
        this.rightLeg.xRot = MathUtils.toRadians(entity.getRightLegPose().getX());
        this.rightLeg.yRot = MathUtils.toRadians(entity.getRightLegPose().getY());
        this.rightLeg.zRot = MathUtils.toRadians(entity.getRightLegPose().getZ());
        this.hat.copyFrom(this.head);
    }

    public static class Slim {

    }
}
