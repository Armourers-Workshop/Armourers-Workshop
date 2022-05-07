package moe.plushie.armourers_workshop.utils.extened;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AWMatrixStack {

    public abstract void translate(double x, double y, double z);

    public abstract void scale(float x, float y, float z);

    public abstract void mul(Quaternion quaternion);

    public abstract void applyPose(Vector4f vector);

    public abstract void applyNormal(Vector3f vector);

    public static AWMatrixStack create() {
        AWMatrix4f pose = AWMatrix4f.createScaleMatrix(1, 1, 1);
        AWMatrix3f normal = AWMatrix3f.createScaleMatrix(1, 1, 1);
        return new AWMatrixStack() {
            @Override
            public void translate(double x, double y, double z) {
                pose.multiply(AWMatrix4f.createTranslateMatrix((float)x, (float)y, (float)z));
            }

            @Override
            public void scale(float x, float y, float z) {
                pose.multiply(AWMatrix4f.createScaleMatrix(x, y, z));
                if (x == y && y == z) {
                    if (x > 0.0F) {
                        return;
                    }
                    normal.multiply(-1.0F);
                }
                float f = 1.0F / x;
                float f1 = 1.0F / y;
                float f2 = 1.0F / z;
                float f3 = TrigUtils.fastInvCubeRoot(f * f1 * f2);
                normal.multiply(AWMatrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
            }

            @Override
            public void mul(Quaternion quaternion) {
                pose.multiply(new AWMatrix4f(quaternion));
                normal.multiply(new AWMatrix3f(quaternion));
            }

            @Override
            public void applyPose(Vector4f vector) {
                vector.transform(pose.unwrap());
            }

            @Override
            public void applyNormal(Vector3f vector) {
                // a perfect solution should use `Vector3f.transform`,
                // but `Vector3f.transform` is client side method.
                Vector4f vector1 = new Vector4f(vector);
                vector1.transform(normal.upcasting());
                vector.set(vector1.x(), vector1.y(), vector1.z());
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    public static AWMatrixStack wrap(MatrixStack matrixStack) {
        return new AWMatrixStack() {
            @Override
            public void translate(double x, double y, double z) {
                matrixStack.translate(x, y, z);
            }

            @Override
            public void scale(float x, float y, float z) {
                matrixStack.scale(x, y, z);
            }

            @Override
            public void mul(Quaternion quaternion) {
                matrixStack.mulPose(quaternion);
            }

            @Override
            public void applyPose(Vector4f vector) {
                vector.transform(matrixStack.last().pose());
            }

            @Override
            public void applyNormal(Vector3f vector) {
                vector.transform(matrixStack.last().normal());
            }
        };
    }
}
