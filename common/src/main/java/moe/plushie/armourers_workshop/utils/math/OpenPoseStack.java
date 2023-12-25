package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Stack;

@SuppressWarnings("unused")
public class OpenPoseStack implements IPoseStack {

    private OpenMatrix4f poseMatrix = OpenMatrix4f.createScaleMatrix(1, 1, 1);
    private OpenMatrix3f normalMatrix = OpenMatrix3f.createScaleMatrix(1, 1, 1);

    private Stack<Pair<OpenMatrix3f, OpenMatrix4f>> stack;

    @Override
    public void pushPose() {
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.push(Pair.of(normalMatrix.copy(), poseMatrix.copy()));
    }

    @Override
    public void popPose() {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Pair<OpenMatrix3f, OpenMatrix4f> pair = stack.pop();
        normalMatrix = pair.getLeft();
        poseMatrix = pair.getRight();
    }

    public void setIdentity() {
        poseMatrix.setIdentity();
        normalMatrix.setIdentity();
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
    public void multiply(IMatrix3f matrix) {
        normalMatrix.multiply(OpenMatrix3f.of(matrix));
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        poseMatrix.multiply(OpenMatrix4f.of(matrix));
    }

    @Override
    public OpenMatrix4f lastPose() {
        return poseMatrix;
    }

    @Override
    public OpenMatrix3f lastNormal() {
        return normalMatrix;
    }
}
