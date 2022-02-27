package moe.plushie.armourers_workshop.core.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MannequinModel<T extends MannequinEntity> extends PlayerModel<T> {

    private Rotations mainPose;

    public MannequinModel(float scale, boolean slim) {
        super(scale, slim);
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
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.mainPose = entity.getBodyPose();
//        if (entity.getItemBySlot(EquipmentSlotType.CHEST).isEmpty()) {
//            if (entity.isCrouching()) {
//                this.cloak.z = 1.4F;
//                this.cloak.y = 1.85F;
//            } else {
//                this.cloak.z = 0.0F;
//                this.cloak.y = 0.0F;
//            }
//        } else if (entity.isCrouching()) {
//            this.cloak.z = 0.3F;
//            this.cloak.y = 0.8F;
//        } else {
//            this.cloak.z = -1.1F;
//            this.cloak.y = -0.85F;
//        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        matrixStack.mulPose(Vector3f.ZP.rotation(getDegree(mainPose.getZ())));
        matrixStack.mulPose(Vector3f.YP.rotation(getDegree(mainPose.getY())));
        matrixStack.mulPose(Vector3f.XP.rotation(getDegree(mainPose.getX())));
        super.renderToBuffer(matrixStack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }

    private float getDegree(double value) {
        value = (value + 360) % 360;
        return (float) ((Math.PI / 180.0) * value);
    }
}
