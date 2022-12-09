package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;

public class AbstractRenderPoseStack implements IPoseStack {

    private static final IPoseStack EMPTY = IPoseStack.newClientInstance();

    public static IPoseStack create() {
        return new AbstractRenderPoseStack();
    }

    @Override
    public void pushPose() {
        RenderSystem.pushMatrix();
    }

    @Override
    public void popPose() {
        RenderSystem.popMatrix();
    }

    @Override
    public void translate(float x, float y, float z) {
        RenderSystem.translated(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        RenderSystem.scalef(x, y, z);
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        RenderSystem.multMatrix(matrix);
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        RenderSystem.multMatrix(new OpenMatrix4f(quaternion));
    }

    @Override
    public IMatrix4f lastPose() {
        return EMPTY.lastPose();
    }

    @Override
    public IMatrix3f lastNormal() {
        return EMPTY.lastNormal();
    }
}
