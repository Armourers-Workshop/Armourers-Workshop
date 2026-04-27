package moe.plushie.armourers_workshop.api.core.math;

public interface IPoseStack {

    void pushPose();

    void popPose();

    void translate(float x, float y, float z);

    void scale(float x, float y, float z);

    void rotate(IQuaternionf quaternion);

    void multiply(IMatrix3f matrix);

    void multiply(IMatrix4f matrix);

    void setIdentity();

    Pose last();

    default void translate(double x, double y, double z) {
        translate((float) x, (float) y, (float) z);
    }

    default void scale(double x, double y, double z) {
        scale((float) x, (float) y, (float) z);
    }

    default void translate(IVector3f translation) {
        translate(translation.x(), translation.y(), translation.z());
    }

    default void scale(IVector3f scalar) {
        scale(scalar.x(), scalar.y(), scalar.z());
    }

    default void translate(IVector3d translation) {
        translate(translation.x(), translation.y(), translation.z());
    }

    default void scale(IVector3d scalar) {
        scale(scalar.x(), scalar.y(), scalar.z());
    }

    interface Pose {

        int properties();

        IMatrix4f pose();

        IMatrix3f normal();

        void set(Pose pose);

        void transformPose(float[] values);

        void transformNormal(float[] values);
    }
}
