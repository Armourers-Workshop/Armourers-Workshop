package moe.plushie.armourers_workshop.utils;

import javax.vecmath.Point2d;

public final class TrigUtils {

    private TrigUtils() {
        throw new IllegalAccessError("Utility class.");
    }

    public static Point2d moveTo(Point2d point, float power, float angle) {
        Point2d newPoint = new Point2d();
        newPoint.x = point.x - power * Math.cos(Math.toRadians(angle));
        newPoint.y = point.y - power * Math.sin(Math.toRadians(angle));
        return newPoint;
    }

    public static double getAngleRadians(double x1, double y1, double x2, double y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.atan2(y, x);
    }

    public static double getAngleRadians(Point2d point1, Point2d point2) {
        return getAngleRadians(point1.x, point1.y, point2.x, point2.y);
    }

    public static double getAngleDegrees(Point2d point1, Point2d point2) {
        return Math.toDegrees(getAngleRadians(point1, point2));
    }

    public static double getAngleDegrees(double x1, double y1, double x2, double y2) {
        return Math.toDegrees(getAngleRadians(x1, y1, x2, y2));
    }
}
