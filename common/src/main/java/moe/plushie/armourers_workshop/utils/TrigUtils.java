package moe.plushie.armourers_workshop.utils;

import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class TrigUtils {

    // Same to Quaternion(Vector3f vec, float f, boolean bl)
    public static Quaternion rotate(Vector3f vec, float f, boolean bl) {
        if (bl) {
            f *= (float) Math.PI / 180;
        }
        float g = MathUtils.sin(f / 2.0f);
        float i = vec.getX() * g;
        float j = vec.getY() * g;
        float k = vec.getZ() * g;
        float r = MathUtils.cos(f / 2.0f);
        return new Quaternion(i, j, k, r);
    }

    // Same to Quaternion(float x, float y, float z, boolean bl)
    public static Quaternion rotate(float x, float y, float z, boolean bl) {
        if (bl) {
            x *= ((float) Math.PI / 180F);
            y *= ((float) Math.PI / 180F);
            z *= ((float) Math.PI / 180F);
        }
        float f = MathUtils.sin(0.5F * x);
        float f1 = MathUtils.cos(0.5F * x);
        float f2 = MathUtils.sin(0.5F * y);
        float f3 = MathUtils.cos(0.5F * y);
        float f4 = MathUtils.sin(0.5F * z);
        float f5 = MathUtils.cos(0.5F * z);
        float i = f * f3 * f5 + f1 * f2 * f4;
        float j = f1 * f2 * f5 - f * f3 * f4;
        float k = f * f2 * f5 + f1 * f3 * f4;
        float r = f1 * f3 * f5 - f * f2 * f4;
        return new Quaternion(i, j, k, r);
    }

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
}
