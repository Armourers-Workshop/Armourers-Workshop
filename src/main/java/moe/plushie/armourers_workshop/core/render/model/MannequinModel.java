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
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.mainPose = entity.getBodyPose();
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        matrixStack.mulPose(Vector3f.ZP.rotation(resolve(mainPose.getZ())));
        matrixStack.mulPose(Vector3f.YP.rotation(resolve(mainPose.getY())));
        matrixStack.mulPose(Vector3f.XP.rotation(resolve(mainPose.getX())));
        super.renderToBuffer(matrixStack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }

    private float resolve(double value) {
        return (float) Math.toRadians((value + 360) % 360);
    }
}
