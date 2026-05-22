package moe.plushie.armourers_workshop.core.skin.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.molang.core.ComputedResult;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

public final class MathHelper {

    public static final double DEG_TO_RAD = Math.PI / 180.0;
    public static final double RAD_TO_DEG = 180.0 / Math.PI;

    public static double cos(double value) {
        return Math.cos((float) value);
    }

    public static double clamp(double d, double e, double f) {
        return d < e ? e : Math.min(d, f);
    }

    public static int floor(double d) {
        int i = (int) d;
        return d < (double) i ? i - 1 : i;
    }

    public static int ceil(double d) {
        int i = (int) d;
        return d > (double) i ? i + 1 : i;
    }

    public static double lerp(double d, double e, double f) {
        return e + d * (f - e);
    }


    /**
     * Special helper function for lerping yaw.
     * <p>
     * This exists because yaw in Minecraft handles its yaw a bit strangely, and can cause incorrect results if lerped without accounting for special-cases
     */
    public static double lerpYaw(double delta, double start, double end) {
        start = wrapDegrees(start);
        end = wrapDegrees(end);
        double diff = start - end;
        end = diff > 180 || diff < -180 ? start + Math.copySign(360 - Math.abs(diff), diff) : end;
        return lerp(delta, start, end);
    }

    public static double wrapDegrees(double d) {
        double e = d % 360.0;
        if (e >= 180.0) {
            e -= 360.0;
        }

        if (e < -180.0) {
            e += 360.0;
        }

        return e;
    }


    public static Result add(final Result a, final Result b) {
        return ComputedResult.valueOf(a.doubleValue() + b.doubleValue());
    }

    public static Result sub(final Result a, final Result b) {
        return ComputedResult.valueOf(a.doubleValue() - b.doubleValue());
    }

    public static Result mul(final Result a, final Result b) {
        return ComputedResult.valueOf(a.doubleValue() * b.doubleValue());
    }

    public static Result div(final Result a, final Result b) {
        // molang allows division by zero, which is always equal to 0
        double divisor = b.doubleValue();
        if (divisor != 0.0) {
            return ComputedResult.valueOf(a.doubleValue() / divisor);
        }
        return Result.ZERO;
    }

    public static Result mod(final Result a, final Result b) {
        // molang allows division by zero, which is always equal to 0
        double divisor = b.doubleValue();
        if (divisor != 0.0) {
            return ComputedResult.valueOf(a.doubleValue() % divisor);
        }
        return Result.ZERO;
    }

    public static Result pow(final Result a, final Result b) {
        return ComputedResult.valueOf(Math.pow(a.doubleValue(), b.doubleValue()));
    }
}
