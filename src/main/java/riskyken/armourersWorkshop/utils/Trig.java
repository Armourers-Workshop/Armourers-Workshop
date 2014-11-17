package riskyken.armourersWorkshop.utils;


public class Trig {

    public static PointD moveTo(PointD point, float speed, float angle) {
        PointD newPoint = new PointD();
        newPoint.x = point.x - speed * Math.cos(DegreesToRadians(angle));
        newPoint.y = point.y - speed * Math.sin(DegreesToRadians(angle));
        return newPoint;
    }

    public static double DegreesToRadians(double degrees) {
        return 2 * Math.PI * degrees / 360.0;
    }

    public static double GetAngle(double x1, double y1, double x2, double y2) {
        double pX = 0;
        double pY = 0;
        double AngleRad = 0;
        double Angle = 0;

        pX = x2 - x1;
        // Adjacent
        pY = y2 - y1;
        // Opposite

        AngleRad = Math.atan2(pY, pX);
        // Radians

        Angle = AngleRad * (180 / Math.PI);
        // Convert Radians to Degrees

        return Angle + 180;
    }
}
