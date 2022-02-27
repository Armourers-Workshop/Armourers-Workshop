package moe.plushie.armourers_workshop.core.render.model;

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
        this.head.xRot = getDegree(entity.getHeadPose().getX());
        this.head.yRot = getDegree(entity.getHeadPose().getY());
        this.head.zRot = getDegree(entity.getHeadPose().getZ());
        this.leftArm.xRot = getDegree(entity.getLeftArmPose().getX());
        this.leftArm.yRot = getDegree(entity.getLeftArmPose().getY());
        this.leftArm.zRot = getDegree(entity.getLeftArmPose().getZ());
        this.rightArm.xRot = getDegree(entity.getRightArmPose().getX());
        this.rightArm.yRot = getDegree(entity.getRightArmPose().getY());
        this.rightArm.zRot = getDegree(entity.getRightArmPose().getZ());
        this.leftLeg.xRot = getDegree(entity.getLeftLegPose().getX());
        this.leftLeg.yRot = getDegree(entity.getLeftLegPose().getY());
        this.leftLeg.zRot = getDegree(entity.getLeftLegPose().getZ());
        this.rightLeg.xRot = getDegree(entity.getRightLegPose().getX());
        this.rightLeg.yRot = getDegree(entity.getRightLegPose().getY());
        this.rightLeg.zRot = getDegree(entity.getRightLegPose().getZ());
        this.hat.copyFrom(this.head);
    }

    private float getDegree(double value) {
        value = (value + 360) % 360;
        return (float) ((Math.PI / 180.0) * value);
    }
}