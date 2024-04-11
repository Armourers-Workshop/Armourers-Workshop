package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.math.IPoseStack;

public class LinkedModelPartPose implements IModelPartPose {

    private IModelPartPose pose;

    public LinkedModelPartPose(IModelPart parent) {
        if (parent != null) {
            this.pose = parent.pose();
        }
    }

    public void linkTo(IModelPart child) {
        this.pose = null;
        if (child != null) {
            this.pose = child.pose();
        }
    }

    @Override
    public float getX() {
        if (pose != null) {
            return pose.getX();
        }
        return 0;
    }

    @Override
    public float getY() {
        if (pose != null) {
            return pose.getY();
        }
        return 0;
    }

    @Override
    public float getZ() {
        if (pose != null) {
            return pose.getZ();
        }
        return 0;
    }

    @Override
    public float getXRot() {
        if (pose != null) {
            return pose.getXRot();
        }
        return 0;
    }

    @Override
    public float getYRot() {
        if (pose != null) {
            return pose.getYRot();
        }
        return 0;
    }

    @Override
    public float getZRot() {
        if (pose != null) {
            return pose.getZRot();
        }
        return 0;
    }

    @Override
    public void setPos(float x, float y, float z) {
        if (pose != null) {
            pose.setPos(x, y, z);
        }
    }

    @Override
    public void setRotation(float xRot, float yRot, float zRot) {
        if (pose != null) {
            pose.setRotation(xRot, yRot, zRot);
        }
    }

    @Override
    public void transform(IPoseStack poseStack) {
        if (pose != null) {
            pose.transform(poseStack);
        }
    }
}
