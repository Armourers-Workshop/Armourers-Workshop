package riskyken.armourersWorkshop.utils;

public class PointD {
    public double x;
    public double y;

    public PointD() {
    }

    public PointD(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointD(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }
}
