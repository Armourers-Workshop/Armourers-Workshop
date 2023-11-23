package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class TrigUtils {

    public static double getAngleRadians(double x1, double y1, double x2, double y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) {
            return 0;
        }
        return Math.atan2(y, x);
    }

//    public static double getAngleRadians(Point2d point1, Point2d point2) {
//        return getAngleRadians(point1.x, point1.y, point2.x, point2.y);
//    }
//
//    public static double getAngleDegrees(Point2d point1, Point2d point2) {
//        return Math.toDegrees(getAngleRadians(point1, point2));
//    }

    public static double getAngleDegrees(double x1, double y1, double x2, double y2) {
        return Math.toDegrees(getAngleRadians(x1, y1, x2, y2));
    }

    public static float fastInvCubeRoot(float f) {
        int i = Float.floatToIntBits(f);
        i = 1419967116 - i / 3;
        float f1 = Float.intBitsToFloat(i);
        f1 = 0.6666667F * f1 + 1.0F / (3.0F * f1 * f1 * f);
        return 0.6666667F * f1 + 1.0F / (3.0F * f1 * f1 * f);
    }

    public static void apply(ITransformf transform, IPoseStack poseStack) {
        IVector3f rotation = transform.getRotation();
        if (rotation != Vector3f.ZERO) {
            IVector3f pivot = transform.getPivot();
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.rotate(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            poseStack.rotate(Vector3f.YP.rotationDegrees(rotation.getY()));
            poseStack.rotate(Vector3f.XP.rotationDegrees(rotation.getX()));
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        IVector3f translate = transform.getTranslate();
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        IVector3f scale = transform.getScale();
        if (scale != Vector3f.ONE) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
    }
}
