package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class ModelPartPose implements IModelPartPose {

    private float x;
    private float y;
    private float z;
    private float xRot;
    private float yRot;
    private float zRot;

    public ModelPartPose(float x, float y, float z, float xRot, float yRot, float zRot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public float getXRot() {
        return xRot;
    }

    @Override
    public float getYRot() {
        return yRot;
    }

    @Override
    public float getZRot() {
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
        if (zRot != 0) {
            poseStack.rotate(Vector3f.ZP.rotation(zRot));
        }
        if (yRot != 0) {
            poseStack.rotate(Vector3f.YP.rotation(yRot));
        }
        if (xRot != 0) {
            poseStack.rotate(Vector3f.XP.rotation(xRot));
        }
    }
}
