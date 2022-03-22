package moe.plushie.armourers_workshop.core.model;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MannequinArmorModel<T extends MannequinEntity> extends BipedModel<T> {

    public MannequinArmorModel(float scale) {
        this(scale, 64, 32);
    }

    public MannequinArmorModel(float scale, int texWidth, int texHeight) {
        super(scale, 0.0F, texWidth, texHeight);
    }

    @Override
    public void setupAnim(T entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        this.head.xRot = resolve(entity.getHeadPose().getX());
        this.head.yRot = resolve(entity.getHeadPose().getY());
        this.head.zRot = resolve(entity.getHeadPose().getZ());
        this.leftArm.xRot = resolve(entity.getLeftArmPose().getX());
        this.leftArm.yRot = resolve(entity.getLeftArmPose().getY());
        this.leftArm.zRot = resolve(entity.getLeftArmPose().getZ());
        this.rightArm.xRot = resolve(entity.getRightArmPose().getX());
        this.rightArm.yRot = resolve(entity.getRightArmPose().getY());
        this.rightArm.zRot = resolve(entity.getRightArmPose().getZ());
        this.leftLeg.xRot = resolve(entity.getLeftLegPose().getX());
        this.leftLeg.yRot = resolve(entity.getLeftLegPose().getY());
        this.leftLeg.zRot = resolve(entity.getLeftLegPose().getZ());
        this.rightLeg.xRot = resolve(entity.getRightLegPose().getX());
        this.rightLeg.yRot = resolve(entity.getRightLegPose().getY());
        this.rightLeg.zRot = resolve(entity.getRightLegPose().getZ());
        this.hat.copyFrom(this.head);
    }

    private float resolve(double value) {
        return (float) Math.toRadians((value + 360) % 360);
    }
}