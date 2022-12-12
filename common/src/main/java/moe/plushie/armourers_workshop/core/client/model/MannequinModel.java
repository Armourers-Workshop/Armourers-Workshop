package moe.plushie.armourers_workshop.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
import moe.plushie.armourers_workshop.compatibility.AbstractPlayerModel;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Rotations;

@Environment(value = EnvType.CLIENT)
public class MannequinModel<T extends MannequinEntity> extends AbstractPlayerModel<T> {

    private Rotations mainPose;

    public MannequinModel(AbstractEntityRendererContext context, float scale, boolean slim) {
        super(context, scale, slim);
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
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.mainPose = entity.getBodyPose();
    }

    @Override
    public void renderToBuffer(PoseStack poseStackIn, VertexConsumer builder, int light, int overlay, float r, float g, float b, float a) {
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        poseStack.rotate(new OpenQuaternionf(mainPose.getX(), mainPose.getY(), mainPose.getZ(), true));
        super.renderToBuffer(poseStackIn, builder, light, overlay, r, g, b, a);
    }
}
