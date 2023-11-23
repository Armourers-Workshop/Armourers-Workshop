package moe.plushie.armourers_workshop.api.math;

public interface ITransformf {

    boolean isIdentity();

    IVector3f getPivot();

    IVector3f getTranslate();

    IVector3f getRotation();

    IVector3f getScale();
}
