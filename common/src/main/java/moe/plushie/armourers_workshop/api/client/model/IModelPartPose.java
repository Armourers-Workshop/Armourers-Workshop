package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.math.IPoseStack;

public interface IModelPartPose {

    void transform(IPoseStack poseStack);

    void setX(float x);

    float getX();

    void setY(float y);

    float getY();

    void setZ(float z);

    float getZ();

    void setXRot(float xRot);

    float getXRot();

    void setYRot(float yRot);

    float getYRot();

    void setZRot(float zRot);

    float getZRot();
}
