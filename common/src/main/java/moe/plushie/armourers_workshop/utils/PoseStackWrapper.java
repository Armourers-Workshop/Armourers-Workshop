package moe.plushie.armourers_workshop.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PoseStackWrapper implements IPoseStack {

    private PoseStack stack;

    public PoseStackWrapper(PoseStack poseStack) {
        this.stack = poseStack;
    }

    public void pushPose() {
        stack.pushPose();
    }

    public void popPose() {
        stack.popPose();
    }

    public void translate(float x, float y, float z) {
        stack.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        stack.scale(x, y, z);
    }

    public void rotate(IQuaternionf quaternion) {
        stack.mulPose(quaternion);
    }

    @Override
    public void multiply(IMatrix3f matrix) {
        stack.mulNormalMatrix(matrix);
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        stack.mulPoseMatrix(matrix);
    }

    public IMatrix4f lastPose() {
        return stack.lastPose();
    }

    public IMatrix3f lastNormal() {
        return stack.lastNormal();
    }

    public void set(PoseStack poseStack) {
        this.stack = poseStack;
    }

    public PoseStack pose() {
        return stack;
    }
}
