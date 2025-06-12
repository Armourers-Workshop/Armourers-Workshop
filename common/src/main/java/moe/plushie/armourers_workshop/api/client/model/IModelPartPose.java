package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

public interface IModelPartPose {

    float x();

    float y();

    float z();

    float xRot();

    float yRot();

    float zRot();

    void setPos(float x, float y, float z);

    void setRotation(float xRot, float yRot, float zRot);

    void transform(IPoseStack poseStack);
}
