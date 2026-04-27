package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.compat.core.math.AbstractMath;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class OpenMath {

    public static final float PI_f = (float) Math.PI;
    public static final float PI2_f = (float) Math.PI * 2.0f;
    public static final float PIHalf_f = (float) Math.PI * 0.5f;

    public static final double PIHalf = Math.PI * 0.5;

    public static final DecimalFormat FLOAT_NUMBER_FORMAT = new DecimalFormat("#.######");
    public static final DecimalFormat DOUBLE_NUMBER_FORMAT = new DecimalFormat("#.###############");

    public static int floori(float value) {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    public static int floori(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }


    public static int ceili(float value) {
        int i = (int) value;
        return value > (float) i ? i + 1 : i;
    }

    public static int ceili(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    public static int roundi(float value) {
        return Math.round(value);
    }

    public static int roundi(double value) {
        return (int) Math.round(value);
    }


    public static float sqrt(float r) {
        return (float) Math.sqrt(r);
    }

    public static double sqrt(double r) {
        return Math.sqrt(r);
    }


    public static float invsqrt(float r) {
        return 1.0f / (float) Math.sqrt(r);
    }

    public static double invsqrt(double r) {
        return 1.0 / Math.sqrt(r);
    }

    public static float sin(float f) {
        return (float) Math.sin(f);
    }

    public static double sin(double f) {
        return Math.sin(f);
    }

    public static float cos(float f) {
        return (float) Math.cos(f);
    }

    public static double cos(double f) {
        return Math.cos(f);
    }


    public static float atan2(float d, float e) {
        return (float) Math.atan2(d, e);
    }

    public static double atan2(double d, double e) {
        return Math.atan2(d, e);
    }

    public static float asin(float r) {
        return (float) Math.asin(r);
    }

    public static double asin(double r) {
        return Math.asin(r);
    }

    public static float safeAsin(float r) {
        if (r <= -1.0f) {
            return -PIHalf_f;
        }
        if (r >= 1.0f) {
            return PIHalf_f;
        }
        return asin(r);
    }

    public static double safeAsin(double r) {
        if (r <= -1.0) {
            return -PIHalf;
        }
        if (r >= 1.0) {
            return PIHalf;
        }
        return asin(r);
    }

    public static float cosFromSin(float sin, float angle) {
        if (AbstractMath.HAS_FAST_MATH) {
            return sin(angle + PIHalf_f);
        }
        // sin(x)^2 + cos(x)^2 = 1
        float cos = sqrt(1.0f - sin * sin);
        float a = angle + PIHalf_f;
        float b = a - (int) (a / PI2_f) * PI2_f;
        if (b < 0.0) {
            b = PI2_f + b;
        }
        if (b >= PI_f) {
            return -cos;
        }
        return cos;
    }


    public static float fma(float a, float b, float c) {
        return AbstractMath.fma(a, b, c);
    }

    public static double fma(double a, double b, double c) {
        return AbstractMath.fma(a, b, c);
    }

    public static int clamp(int value, int minValue, int maxValue) {
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

    public static float min(float a, float b, float c) {
        return Math.min(a, Math.min(b, c));
    }

    public static double min(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    public static float max(float a, float b, float c) {
        return Math.max(a, Math.max(b, c));
    }

    public static double max(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }

    public static float lerp(float position, float a, float b) {
        return fma(position, b - a, a);
    }

    public static double lerp(double position, double a, double b) {
        return fma(position, b - a, a);
    }

    public static OpenVector3f lerp(float position, OpenVector3f a, OpenVector3f b) {
        if (position == 0.0f) {
            return a;
        }
        if (position == 1.0f) {
            return b;
        }
        var x = lerp(position, a.x, b.x);
        var y = lerp(position, a.y, b.y);
        var z = lerp(position, a.z, b.z);
        return new OpenVector3f(x, y, z);
    }

    public static OpenQuaternionf lerp(float position, OpenQuaternionf a, OpenQuaternionf b) {
        if (position == 0.0f) {
            return a;
        }
        if (position == 1.0f) {
            return b;
        }
        var quat = new OpenQuaternionf(lerp(position, a.x(), b.x()), lerp(position, a.y(), b.y()), lerp(position, a.z(), b.z()), lerp(position, a.w(), b.w()));
        quat.normalize();
        return quat;
    }

    public static float rotLerp(float position, float a, float n) {
        return fma(position, wrapDegrees(n - a), a);
    }

    public static double rotLerp(double position, double a, double n) {
        return fma(position, wrapDegrees(n - a), a);
    }

    /// Generates a value from a given Catmull-Rom spline range with Centripetal parameterization (alpha=0.5)
    ///
    /// Per standard implementation, this generates a spline curve over control points p1-p2, with p0 and p3
    /// acting as curve anchors.
    /// We then apply the delta to determine the point on the generated spline to return.
    ///
    /// @see <a href="https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline">Wikipedia</a>
    public static float catmullrom(float t, float p0, float p1, float p2, float p3) {
        return 0.5f * (2.0f * p1 + (p2 - p0) * t +
                (2.0f * p0 - 5.0f * p1 + 4.0f * p2 - p3) * t * t +
                (3.0f * p1 - p0 - 3.0f * p2 + p3) * t * t * t);
    }

    public static float toDegrees(float a) {
        return (float) Math.toDegrees(a);
    }

    public static double toDegrees(double a) {
        return Math.toDegrees(a);
    }


    public static float toRadians(float value) {
        return (float) Math.toRadians((value + 360) % 360);
    }

    public static double toRadians(double value) {
        return Math.toRadians((value + 360) % 360);
    }

    public static double getAngleDegrees(double x1, double y1, double x2, double y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) {
            return 0;
        }
        return Math.toDegrees(Math.atan2(y, x));
    }

    public static int wrapDegrees(int r) {
        int i = r % 360;
        if (i >= 180) {
            i -= 360;
        }
        if (i < -180) {
            i += 360;
        }
        return i;
    }

    public static float wrapDegrees(float r) {
        float f = r % 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }
        if (f < -180.0F) {
            f += 360.0F;
        }
        return f;
    }

    public static double wrapDegrees(double r) {
        double d0 = r % 360.0D;
        if (d0 >= 180.0D) {
            d0 -= 360.0D;
        }
        if (d0 < -180.0D) {
            d0 += 360.0D;
        }
        return d0;
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


    public static int roundToward(int i, int j) {
        return positiveCeilDiv(i, j) * j;
    }

    public static int positiveCeilDiv(int i, int j) {
        return -Math.floorDiv(-i, j);
    }

    public static double random() {
        return Math.random();
    }

    public static float randomf() {
        return (float) Math.random();
    }

    public static void normalize(float[] values) {
        float f = fma(values[0], values[0], fma(values[1], values[1], values[2] * values[2]));
        float g = fastInvCubeRoot(f);
        values[0] *= g;
        values[1] *= g;
        values[2] *= g;
    }


    public static String format(String format, Object... args) {
        var builder = new StringBuilder();
        var pattern = Pattern.compile("(%[a-zA-Z0-9.+-]+)");
        var matcher = pattern.matcher(format);
        int charIndex = 0;
        int argIndex = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            var command = format.substring(start, end);
            // resolve %f %lf
            if (command.equals("%f")) {
                command = "%s";
                args[argIndex] = FLOAT_NUMBER_FORMAT.format(args[argIndex]);
            }
            if (command.equals("%lf")) {
                command = "%s";
                args[argIndex] = DOUBLE_NUMBER_FORMAT.format(args[argIndex]);
            }
            builder.append(format, charIndex, start);
            builder.append(command);
            charIndex = end;
            argIndex += 1;
        }
        builder.append(format, charIndex, format.length());
        return String.format(builder.toString(), args);
    }
}
