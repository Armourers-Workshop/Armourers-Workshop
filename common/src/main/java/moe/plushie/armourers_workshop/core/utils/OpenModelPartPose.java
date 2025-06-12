package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;

public class OpenModelPartPose implements IModelPartPose {

    private float x;
    private float y;
    private float z;
    private float xRot;
    private float yRot;
    private float zRot;

    public OpenModelPartPose() {
        this(0, 0, 0, 0, 0, 0);
    }

    public OpenModelPartPose(float x, float y, float z, float xRot, float yRot, float zRot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float z() {
        return z;
    }

    @Override
    public float xRot() {
        return xRot;
    }

    @Override
    public float yRot() {
        return yRot;
    }

    @Override
    public float zRot() {
        return zRot;
    }

    @Override
    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void setRotation(float xRot, float yRot, float zRot) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }

    @Override
    public void transform(IPoseStack poseStack) {
        if (x != 0 || y != 0 || z != 0) {
            poseStack.translate(x, y, z);
        }
        if (xRot != 0 || yRot != 0 || zRot != 0) {
            poseStack.rotate(OpenQuaternionf.fromEulerAnglesZYX(zRot, yRot, xRot));
        }
    }
}
