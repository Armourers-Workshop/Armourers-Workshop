package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.utils.MathUtils;

public class PoseStack extends AbstractPoseStack {

    private final Matrix4f poseMatrix = Matrix4f.createScaleMatrix(1, 1, 1);
    private final Matrix3f normalMatrix = Matrix3f.createScaleMatrix(1, 1, 1);

    @Override
    public void pushPose() {
    }

    @Override
    public void popPose() {
    }

    @Override
    public void translate(float x, float y, float z) {
        poseMatrix.multiply(Matrix4f.createTranslateMatrix(x, y, z));
    }

    @Override
    public void scale(float x, float y, float z) {
        poseMatrix.multiply(Matrix4f.createScaleMatrix(x, y, z));
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
        normalMatrix.multiply(Matrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        poseMatrix.rotate(quaternion);
        normalMatrix.rotate(quaternion);
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        poseMatrix.multiply(Matrix4f.of(matrix));
        normalMatrix.multiply(Matrix3f.of(matrix));
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
        PoseStack stack = new PoseStack();
        stack.poseMatrix.multiply(poseMatrix);
        stack.normalMatrix.multiply(normalMatrix);
        return stack;
    }
}
