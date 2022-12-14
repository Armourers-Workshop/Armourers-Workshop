package moe.plushie.armourers_workshop.utils.math;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class OpenPoseStack implements IPoseStack {

    private final OpenMatrix4f poseMatrix = OpenMatrix4f.createScaleMatrix(1, 1, 1);
    private final OpenMatrix3f normalMatrix = OpenMatrix3f.createScaleMatrix(1, 1, 1);

    @Override
    public void pushPose() {

    }

    @Override
    public void popPose() {

    }

    @Override
    public void translate(float x, float y, float z) {
        poseMatrix.multiply(OpenMatrix4f.createTranslateMatrix(x, y, z));
    }

    @Override
    public void scale(float x, float y, float z) {
        poseMatrix.multiply(OpenMatrix4f.createScaleMatrix(x, y, z));
        if (x == y && y == z) {
            if (x > 0.0F) {
                return;
            }
            normalMatrix.multiply(-1.0F);
        }
        float f = 1.0F / x;
        float f1 = 1.0F / y;
        float f2 = 1.0F / z;
        float f3 = MathUtils.fastInvCubeRoot(f * f1 * f2);
        normalMatrix.multiply(OpenMatrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        poseMatrix.rotate(quaternion);
        normalMatrix.rotate(quaternion);
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        poseMatrix.multiply(OpenMatrix4f.of(matrix));
        normalMatrix.multiply(new OpenMatrix3f(matrix));
    }

    @Override
    public void multiply(IPoseStack poseStack) {
        poseMatrix.multiply(poseStack.lastPose());
        normalMatrix.multiply(poseStack.lastNormal());
    }

    @Override
    public IMatrix4f lastPose() {
        return poseMatrix;
    }

    @Override
    public IMatrix3f lastNormal() {
        return normalMatrix;
    }

    @Override
    public IPoseStack copy() {
        OpenPoseStack stack = new OpenPoseStack();
        stack.poseMatrix.multiply(poseMatrix);
        stack.normalMatrix.multiply(normalMatrix);
        return stack;
    }

    @Environment(value = EnvType.CLIENT)
    @Override
    public PoseStack cast() {
        IPoseStack poseStack = AbstractPoseStack.empty();
        poseStack.lastPose().multiply(poseMatrix);
        poseStack.lastNormal().multiply(normalMatrix);
        return poseStack.cast();
    }
}
