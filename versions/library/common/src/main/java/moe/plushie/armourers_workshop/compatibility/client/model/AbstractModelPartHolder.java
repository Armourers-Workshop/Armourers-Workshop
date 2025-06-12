package moe.plushie.armourers_workshop.compatibility.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import net.minecraft.client.model.geom.ModelPart;

public class AbstractModelPartHolder implements IModelPart, IModelPartPose {

    private String name;
    private final ModelPart modelPart;

    public AbstractModelPartHolder(ModelPart modelPart) {
        this.modelPart = modelPart;
    }

    public static AbstractModelPartHolder of(ModelPart part) {
        return new AbstractModelPartHolder(part);
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
        float x = x();
        float y = y();
        float z = z();
        if (x != 0 || y != 0 || z != 0) {
            poseStack.translate(x, y, z);
        }
        float xRot = xRot();
        float yRot = yRot();
        float zRot = zRot();
        if (xRot != 0 || yRot != 0 || zRot != 0) {
            poseStack.rotate(OpenQuaternionf.fromEulerAnglesZYX(zRot, yRot, xRot));
        }
    }

    @Override
    public float x() {
        return modelPart.x;
    }

    @Override
    public float y() {
        return modelPart.y;
    }

    @Override
    public float z() {
        return modelPart.z;
    }

    @Override
    public float xRot() {
        return modelPart.xRot;
    }

    @Override
    public float yRot() {
        return modelPart.yRot;
    }

    @Override
    public float zRot() {
        return modelPart.zRot;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setPos(float x, float y, float z) {
        modelPart.x = x;
        modelPart.y = y;
        modelPart.z = z;
    }

    @Override
    public void setRotation(float xRot, float yRot, float zRot) {
        modelPart.xRot = xRot;
        modelPart.yRot = yRot;
        modelPart.zRot = zRot;
    }
}
