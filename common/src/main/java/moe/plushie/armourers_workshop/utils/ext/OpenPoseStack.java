package moe.plushie.armourers_workshop.utils.ext;

import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;

public abstract class OpenPoseStack {

    public static OpenPoseStack create() {
        OpenMatrix4f pose = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        OpenMatrix3f normal = OpenMatrix3f.createScaleMatrix(1, 1, 1);
        return new OpenPoseStack() {
            @Override
            public void translate(float x, float y, float z) {
                pose.multiply(OpenMatrix4f.createTranslateMatrix(x, y, z));
            }

            @Override
            public void scale(float x, float y, float z) {
                pose.multiply(OpenMatrix4f.createScaleMatrix(x, y, z));
                if (x == y && y == z) {
                    if (x > 0.0F) {
                        return;
                    }
                    normal.multiply(-1.0F);
                }
                float f = 1.0F / x;
                float f1 = 1.0F / y;
                float f2 = 1.0F / z;
                float f3 = MathUtils.fastInvCubeRoot(f * f1 * f2);
                normal.multiply(OpenMatrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
            }

            @Override
            public void mul(Quaternion quaternion) {
                pose.multiply(new OpenMatrix4f(quaternion));
                normal.multiply(new OpenMatrix3f(quaternion));
            }

            @Override
            public void applyPose(Vector4f vector) {
                vector.transform(pose);
            }

            @Override
            public void applyNormal(Vector3f vector) {
                vector.transform(normal);
            }
        };
    }

    public abstract void translate(float x, float y, float z);

    public abstract void scale(float x, float y, float z);

    public abstract void mul(Quaternion quaternion);

    public abstract void applyPose(Vector4f vector);

    public abstract void applyNormal(Vector3f vector);
}
