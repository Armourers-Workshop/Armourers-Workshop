package moe.plushie.armourers_workshop.compatibility;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.utils.RenderSystem;

public class AbstractRenderPoseStack {

    private static final Matrix4f EMPTY_POSE = Matrix4f.createScaleMatrix(1, 1, 1);
    private static final Matrix3f EMPTY_NORMAL = Matrix3f.createScaleMatrix(1, 1, 1);

    public void pushPose() {
        RenderSystem.pushMatrix();
    }

    public void translate(float x, float y, float z) {
        RenderSystem.translated(x, y, z);
    }

    public void scale(float x, float y, float z) {
        RenderSystem.scalef(x, y, z);
    }

    public void mulPose(Quaternion quaternion) {
        RenderSystem.multMatrix(new Matrix4f(quaternion));
    }

    public void mulPose(Matrix4f matrix) {
        RenderSystem.multMatrix(matrix);
    }

    public void popPose() {
        RenderSystem.popMatrix();
    }

    public void apply() {
    }

    public Matrix4f lastPose() {
        return EMPTY_POSE;
    }

    public Matrix3f lastNormal() {
        return EMPTY_NORMAL;
    }
}
