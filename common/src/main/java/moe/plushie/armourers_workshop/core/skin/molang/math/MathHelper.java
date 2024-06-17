package moe.plushie.armourers_workshop.core.skin.molang.math;

public final class MathHelper {

    private MathHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static float wrapDegrees(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapDegrees(double value) {
        value = value % 360.0D;

        if (value >= 180.0D) {
            value -= 360.0D;
        }

        if (value < -180.0D) {
            value += 360.0D;
        }

        return value;
    }

    /**
     * Adjust the angle so that his value is in range [-180;180[
     */
    public static int wrapDegrees(int angle) {
        angle = angle % 360;

        if (angle >= 180) {
            angle -= 360;
        }

        if (angle < -180) {
            angle += 360;
        }

        return angle;
    }

    public static int clamp(int x, int min, int max) {
        return Math.max(Math.min(x, max), min);
    }

    public static float clamp(float x, float min, float max) {
        return Math.max(Math.min(x, max), min);
    }

    public static double clamp(double x, double min, double max) {
        return Math.max(Math.min(x, max), min);
    }

    public static int cycler(int x, int min, int max) {
        if (x > max) {
            return min;
        }

        return x < min ? max : x;
    }

    public static float cycler(float x, float min, float max) {
        if (x > max) {
            return min;
        }

        return x < min ? max : x;
    }

    public static double cycler(double x, double min, double max) {
        if (x > max) {
            return min;
        }

        return x < min ? max : x;
    }
}
