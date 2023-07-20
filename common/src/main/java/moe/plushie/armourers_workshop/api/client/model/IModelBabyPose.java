package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.math.IVector3f;

public interface IModelBabyPose {

    float getHeadScale();

    IVector3f getHeadOffset();

    static IModelBabyPose of(float headScale, IVector3f headOffset) {
        return new IModelBabyPose() {
            @Override
            public float getHeadScale() {
                return headScale;
            }

            @Override
            public IVector3f getHeadOffset() {
                return headOffset;
            }
        };
    }
}
