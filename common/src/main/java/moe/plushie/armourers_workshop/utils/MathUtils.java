package moe.plushie.armourers_workshop.utils;

public class MathUtils {
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


    public static double absMax(double a, double b) {
        if (a < 0.0D) {
            a = -a;
        }

        if (b < 0.0D) {
            b = -b;
        }

        return a > b ? a : b;
    }


    public static float lerp(float p_219799_0_, float p_219799_1_, float p_219799_2_) {
        return p_219799_1_ + p_219799_0_ * (p_219799_2_ - p_219799_1_);
    }

    public static double lerp(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
        return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
    }

    public static float rotLerp(float p_219805_0_, float p_219805_1_, float p_219805_2_) {
        return p_219805_1_ + p_219805_0_ * wrapDegrees(p_219805_2_ - p_219805_1_);
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
        float f = 0.5F * v;
        int i = Float.floatToIntBits(v);
        i = 1597463007 - (i >> 1);
        v = Float.intBitsToFloat(i);
        return v * (1.5F - f * v * v);
    }

    public static double fastInvSqrt(double p_181161_0_) {
        double d0 = 0.5D * p_181161_0_;
        long i = Double.doubleToRawLongBits(p_181161_0_);
        i = 6910469410427058090L - (i >> 1);
        p_181161_0_ = Double.longBitsToDouble(i);
        return p_181161_0_ * (1.5D - d0 * p_181161_0_ * p_181161_0_);
    }

    public static float fastInvCubeRoot(float p_226166_0_) {
        int i = Float.floatToIntBits(p_226166_0_);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
        return 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
    }

    public static float toRadians(double value) {
        return (float) Math.toRadians((value + 360) % 360);
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
