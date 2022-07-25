package moe.plushie.armourers_workshop.utils;

import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public class TrigUtils {

    // Same to Quaternion(float x, float y, float z, boolean degrees)
    public static Quaternion rotate(float x, float y, float z, boolean degrees) {
        if (degrees) {
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

    public static float fastInvCubeRoot(float p_226166_0_) {
        int i = Float.floatToIntBits(p_226166_0_);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
        return 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
    }

    public static Direction rotate(Direction dir, Rotation rotation) {
        if (dir != null) {
            return rotation.rotate(dir);
        }
        return dir;
    }

    public static OptionalDirection rotate(OptionalDirection dir, Rotation rotation) {
        if (dir.getDirection() != null) {
            return OptionalDirection.of(rotation.rotate(dir.getDirection()));
        }
        return dir;
    }
}
