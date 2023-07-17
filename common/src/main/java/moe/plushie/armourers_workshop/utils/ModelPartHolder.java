package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;

public class ModelPartHolder implements IModelPart, IModelPartPose {

    private final ModelPart modelPart;

    public ModelPartHolder(ModelPart modelPart) {
        this.modelPart = modelPart;
    }

    public static ModelPartHolder of(ModelPart part) {
        return new ModelPartHolder(part);
    }

    @Override
    public boolean isVisible() {
        return modelPart.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        modelPart.visible = visible;
    }

    @Override
    public IModelPartPose pose() {
        return this;
    }

    @Override
    public void transform(IPoseStack poseStack) {
        float x = getX();
        float y = getY();
        float z = getZ();
        if (x != 0 || y != 0 || z != 0) {
            poseStack.translate(x, y, z);
        }
        float zRot = getZRot();
        if (zRot != 0) {
            poseStack.rotate(Vector3f.ZP.rotation(zRot));
        }
        float yRot = getYRot();
        if (yRot != 0) {
            poseStack.rotate(Vector3f.YP.rotation(yRot));
        }
        float xRot = getXRot();
        if (xRot != 0) {
            poseStack.rotate(Vector3f.XP.rotation(xRot));
        }
    }

    @Override
    public void setX(float x) {
        modelPart.x = x;
    }

    @Override
    public float getX() {
        return modelPart.x;
    }

    @Override
    public void setY(float y) {
        modelPart.y = y;
    }

    @Override
    public float getY() {
        return modelPart.y;
    }

    @Override
    public void setZ(float z) {
        modelPart.z = z;
    }

    @Override
    public float getZ() {
        return modelPart.z;
    }

    @Override
    public void setXRot(float xRot) {
        modelPart.xRot = xRot;
    }

    @Override
    public float getXRot() {
        return modelPart.xRot;
    }

    @Override
    public void setYRot(float yRot) {
        modelPart.yRot = yRot;
    }

    @Override
    public float getYRot() {
        return modelPart.yRot;
    }

    @Override
    public void setZRot(float zRot) {
        modelPart.zRot = zRot;
    }

    @Override
    public float getZRot() {
        return modelPart.zRot;
    }
}
