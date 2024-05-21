package moe.plushie.armourers_workshop.utils;

public class MathUtils {

    public static final float SCALE = 0.0625f; // 1 / 16f;

    public static final float PI = (float) Math.PI;
    public static final float PI_D2 = (float) Math.PI * 0.5f;
    public static final float PI_M2 = (float) Math.PI * 2.0f;

    private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASIN_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];

    private static final float[] SIN = new float[65536];

    public static int clamp(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        }
        if (value > maxValue) {
            return maxValue;
        }
        return value;
    }

    public static long clamp(long value, long minValue, long maxValue) {
        if (value < minValue) {
            return minValue;
        }
        if (value > maxValue) {
            return maxValue;
        }
        return value;
    }

    public static float clamp(float value, float minValue, float maxValue) {
        if (value < minValue) {
            return minValue;
        }
        if (value > maxValue) {
            return maxValue;
        }
        return value;
    }

    public static double clamp(double value, double minValue, double maxValue) {
        if (value < minValue) {
            return minValue;
        }
        if (value > maxValue) {
            return maxValue;
        }
        return value;
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    public static int ceil(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    public static float sqrt(float p_76129_0_) {
        return (float) Math.sqrt(p_76129_0_);
    }

    public static float sqrt(double p_76133_0_) {
        return (float) Math.sqrt(p_76133_0_);
    }

    public static float sin(float f) {
        return SIN[(int) (f * 10430.378F) & '\uffff'];
    }

    public static float cos(float f) {
        return SIN[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static float cosFromSin(float sin, float angle) {
//        if (Options.FASTMATH)
//            return sin(angle + PIHalf_f);
        // sin(x)^2 + cos(x)^2 = 1
        float cos = sqrt(1.0f - sin * sin);
        float a = angle + PI_D2;
        float b = a - (int) (a / PI_M2) * PI_M2;
        if (b < 0.0)
            b = PI_M2 + b;
        if (b >= PI)
            return -cos;
        return cos;
    }

    public static double absMax(double a, double b) {
        if (a < 0.0D) {
            a = -a;
        }

        if (b < 0.0D) {
            b = -b;
        }

        return a > b ? a : b;
    }


    public static float lerp(float position, float a, float b) {
        return a + (b - a) * position;
    }

    public static double lerp(double position, double a, double b) {
        return a + (b - a) * position;
    }

    /**
     * Special interpolation method for interpolating yaw. The problem with yaw, is that it may go in the "wrong"
     * direction when having, for example, -170 (as a) and 170 (as b) degress or other way around (170 and -170). This
     * interpolation method fixes this problem.
     */
    public static float lerpYaw(float position, float a, float b) {
        a = wrapDegrees(a);
        b = wrapDegrees(b);

        return lerp(position, a, normalizeYaw(a, b));
    }

    /**
     * Special interpolation method for interpolating yaw. The problem with yaw, is that it may go in the "wrong"
     * direction when having, for example, -170 (as a) and 170 (as b) degress or other way around (170 and -170). This
     * interpolation method fixes this problem.
     */
    public static double lerpYaw(double position, double a, double b) {
        a = wrapDegrees(a);
        b = wrapDegrees(b);

        return lerp(a, normalizeYaw(a, b), position);
    }

    public static float rotLerp(float position, float a, float n) {
        return a + position * wrapDegrees(n - a);
    }

    public static int wrapDegrees(int p_188209_0_) {
        int i = p_188209_0_ % 360;
        if (i >= 180) {
            i -= 360;
        }

        if (i < -180) {
            i += 360;
        }

        return i;
    }

    public static float wrapDegrees(float p_76142_0_) {
        float f = p_76142_0_ % 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static double wrapDegrees(double p_76138_0_) {
        double d0 = p_76138_0_ % 360.0D;
        if (d0 >= 180.0D) {
            d0 -= 360.0D;
        }

        if (d0 < -180.0D) {
            d0 += 360.0D;
        }

        return d0;
    }


    /**
     * Normalize yaw rotation (argument {@code b}) based on the previous yaw rotation.
     */
    public static float normalizeYaw(float a, float b) {
        float diff = a - b;

        if (diff > 180 || diff < -180) {
            diff = Math.copySign(360 - Math.abs(diff), diff);

            return a + diff;
        }

        return b;
    }

    /**
     * Normalize yaw rotation (argument {@code b}) based on the previous yaw rotation.
     */
    public static double normalizeYaw(double a, double b) {
        double diff = a - b;

        if (diff > 180 || diff < -180) {
            diff = Math.copySign(360 - Math.abs(diff), diff);

            return a + diff;
        }

        return b;
    }

    public static float acos(float e) {
        return (float) Math.acos(e);
    }

    public static float atan2(float d, float e) {
        return (float) Math.atan2(d, e);
    }

    public static double atan2(double d, double e) {
        double g;
        boolean bl3;
        boolean bl2;
        boolean bl;
        double f = e * e + d * d;
        if (Double.isNaN(f)) {
            return Double.NaN;
        }
        boolean bl4 = bl = d < 0.0;
        if (bl) {
            d = -d;
        }
        boolean bl5 = bl2 = e < 0.0;
        if (bl2) {
            e = -e;
        }
        boolean bl6 = bl3 = d > e;
        if (bl3) {
            g = e;
            e = d;
            d = g;
        }
        g = fastInvSqrt(f);
        e *= g;
        double h = FRAC_BIAS + (d *= g);
        int i = (int) Double.doubleToRawLongBits(h);
        double j = ASIN_TAB[i];
        double k = COS_TAB[i];
        double l = h - FRAC_BIAS;
        double m = d * k - e * l;
        double n = (6.0 + m * m) * m * 0.16666666666666666;
        double o = j + n;
        if (bl3) {
            o = 1.5707963267948966 - o;
        }
        if (bl2) {
            o = Math.PI - o;
        }
        if (bl) {
            o = -o;
        }
        return o;
    }

    public static float fastInvSqrt(float v) {
        float f = 0.5f * v;
        int i = Float.floatToIntBits(v);
        i = 1597463007 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5f - f * v * v);
    }

    public static double fastInvSqrt(double d) {
        double e = 0.5 * d;
        long l = Double.doubleToRawLongBits(d);
        l = 6910469410427058090L - (l >> 1);
        d = Double.longBitsToDouble(l);
        d *= 1.5 - e * d * d;
        return d;
    }

    public static float fastInvCubeRoot(float f) {
        int i = Float.floatToIntBits(f);
        i = 1419967116 - i / 3;
        float g = Float.intBitsToFloat(i);
        g = 0.6666667F * g + 1.0F / (3.0F * g * g * f);
        g = 0.6666667F * g + 1.0F / (3.0F * g * g * f);
        return g;
    }

    public static double toDegrees(double angrad) {
        return Math.toDegrees(angrad);
    }

    public static float toRadians(double value) {
        return (float) Math.toRadians((value + 360) % 360);
    }

    public static void normalize(float[] values) {
        float f = values[0] * values[0] + values[1] * values[1] + values[2] * values[2];
        float g = fastInvCubeRoot(f);
        values[0] *= g;
        values[1] *= g;
        values[2] *= g;
    }

    public static float fma(float a, float b, float c) {
//        if (Runtime.HAS_Math_fma)
//            return java.lang.Math.fma(a, b, c);
        return a * b + c;
    }

    public static int roundToward(int i, int j) {
        return positiveCeilDiv(i, j) * j;
    }

    public static int positiveCeilDiv(int i, int j) {
        return -Math.floorDiv(-i, j);
    }

    public static boolean equal(float f, float g) {
        return Math.abs(g - f) < 1.0E-5f;
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d = (double) i / 256.0;
            double e = Math.asin(d);
            COS_TAB[i] = Math.cos(e);
            ASIN_TAB[i] = e;
        }
        for (int i = 0; i < SIN.length; ++i) {
            SIN[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }
    }
}
