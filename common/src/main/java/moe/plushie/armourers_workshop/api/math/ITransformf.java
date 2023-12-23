package moe.plushie.armourers_workshop.api.math;

/**
 * <a href="https://learnopengl.com/Getting-started/Transformations">ref1</a>
 * <a href="https://stackoverflow.com/questions/58398147/what-is-the-correct-order-of-transformations-when-calculating-matrices-in-opengl">ref2</a>
 * M = T * P * R * -P * S * V
 */
public interface ITransformf {

    boolean isIdentity();

    IVector3f getTranslate();

    IVector3f getRotation();

    IVector3f getScale();

    IVector3f getOffset();

    IVector3f getPivot();

    void apply(IPoseStack poseStack);
}
