package moe.plushie.armourers_workshop.utils.math;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class OpenPoseStackC implements IPoseStack {

    private PoseStack poseStack;

    public OpenPoseStackC(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    public void reset(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    @Override
    public void pushPose() {
        poseStack.pushPose();
    }

    @Override
    public void popPose() {
        poseStack.popPose();
    }

    @Override
    public void translate(float x, float y, float z) {
        poseStack.translate(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        poseStack.scale(x, y, z);
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        poseStack.mulPose(quaternion);
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        poseStack.mulPoseMatrix(matrix);
    }

    @Override
    public IMatrix4f lastPose() {
        return poseStack.lastPose();
    }

    @Override
    public IMatrix3f lastNormal() {
        return poseStack.lastNormal();
    }
}
