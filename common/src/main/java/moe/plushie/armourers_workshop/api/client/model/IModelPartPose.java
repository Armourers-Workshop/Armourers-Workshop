package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.math.IPoseStack;

public interface IModelPartPose {

    float getX();

    float getY();

    float getZ();

    float getXRot();

    float getYRot();

    float getZRot();

    void setPos(float x, float y, float z);

    void setRotation(float xRot, float yRot, float zRot);

    void transform(IPoseStack poseStack);
}
