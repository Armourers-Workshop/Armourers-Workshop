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
        this.head.xRot = ((float) Math.PI / 180F) * entity.getHeadPose().getX();
        this.head.yRot = ((float) Math.PI / 180F) * entity.getHeadPose().getY();
        this.head.zRot = ((float) Math.PI / 180F) * entity.getHeadPose().getZ();
        this.head.setPos(0.0F, 1.0F, 0.0F);
        this.body.xRot = ((float) Math.PI / 180F) * entity.getBodyPose().getX();
        this.body.yRot = ((float) Math.PI / 180F) * entity.getBodyPose().getY();
        this.body.zRot = ((float) Math.PI / 180F) * entity.getBodyPose().getZ();
        this.leftArm.xRot = ((float) Math.PI / 180F) * entity.getLeftArmPose().getX();
        this.leftArm.yRot = ((float) Math.PI / 180F) * entity.getLeftArmPose().getY();
        this.leftArm.zRot = ((float) Math.PI / 180F) * entity.getLeftArmPose().getZ();
        this.rightArm.xRot = ((float) Math.PI / 180F) * entity.getRightArmPose().getX();
        this.rightArm.yRot = ((float) Math.PI / 180F) * entity.getRightArmPose().getY();
        this.rightArm.zRot = ((float) Math.PI / 180F) * entity.getRightArmPose().getZ();
        this.leftLeg.xRot = ((float) Math.PI / 180F) * entity.getLeftLegPose().getX();
        this.leftLeg.yRot = ((float) Math.PI / 180F) * entity.getLeftLegPose().getY();
        this.leftLeg.zRot = ((float) Math.PI / 180F) * entity.getLeftLegPose().getZ();
        this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
        this.rightLeg.xRot = ((float) Math.PI / 180F) * entity.getRightLegPose().getX();
        this.rightLeg.yRot = ((float) Math.PI / 180F) * entity.getRightLegPose().getY();
        this.rightLeg.zRot = ((float) Math.PI / 180F) * entity.getRightLegPose().getZ();
        this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
        this.hat.copyFrom(this.head);
    }
}