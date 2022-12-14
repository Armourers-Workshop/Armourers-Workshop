package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.math.ITransformf;

public interface IJointTransform {

    void setTransforms(ITransformf[] transforms) ;

    ITransformf[] getTransforms();
}
