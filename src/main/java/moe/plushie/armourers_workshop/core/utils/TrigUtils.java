package moe.plushie.armourers_workshop.core.utils;

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
}
