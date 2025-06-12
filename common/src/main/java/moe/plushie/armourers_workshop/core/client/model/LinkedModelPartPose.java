package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

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
    public float x() {
        if (pose != null) {
            return pose.x();
        }
        return 0;
    }

    @Override
    public float y() {
        if (pose != null) {
            return pose.y();
        }
        return 0;
    }

    @Override
    public float z() {
        if (pose != null) {
            return pose.z();
        }
        return 0;
    }

    @Override
    public float xRot() {
        if (pose != null) {
            return pose.xRot();
        }
        return 0;
    }

    @Override
    public float yRot() {
        if (pose != null) {
            return pose.yRot();
        }
        return 0;
    }

    @Override
    public float zRot() {
        if (pose != null) {
            return pose.zRot();
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
