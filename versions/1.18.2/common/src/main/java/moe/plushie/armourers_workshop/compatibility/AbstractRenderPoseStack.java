package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

public class AbstractRenderPoseStack {

    final PoseStack poseStack;
    public AbstractRenderPoseStack() {
        poseStack = RenderSystem.getModelViewStack();
    }

    public void pushPose() {
        poseStack.pushPose();
    }

    public void translate(float x, float y, float z) {
        poseStack.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        poseStack.scale(x, y, z);
    }

    public void mulPose(Quaternion quaternion) {
        poseStack.mulPose(quaternion);
    }

    public void mulPose(Matrix4f matrix) {
        poseStack.mulPoseMatrix(matrix);
    }

    public void popPose() {
        poseStack.popPose();
    }

    public void apply() {
        RenderSystem.applyModelViewMatrix();
    }
}
