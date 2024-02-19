package moe.plushie.armourers_workshop.api.math;

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

    interface Pose {

        int properties();

        IMatrix4f pose();

        IMatrix3f normal();

        void set(Pose pose);

        void transformPose(float[] values);

        void transformNormal(float[] values);
    }
}
