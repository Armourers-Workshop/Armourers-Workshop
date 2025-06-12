package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.core.math.IVector3f;

public interface IModelBabyPose {

    float headScale();

    IVector3f headOffset();

    static IModelBabyPose of(float headScale, IVector3f headOffset) {
        return new IModelBabyPose() {
            @Override
            public float headScale() {
                return headScale;
            }

            @Override
            public IVector3f headOffset() {
                return headOffset;
            }
        };
    }
}
