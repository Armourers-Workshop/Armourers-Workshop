package moe.plushie.armourers_workshop.core.skin.molang.impl;

import moe.plushie.armourers_workshop.utils.MathUtils;

public final class MathHelper {

    public static final float DEG_TO_RAD = 0.017453292F;
    public static final float RAD_TO_DEG = 57.295776F;

    public static double cos(double value) {
        return MathUtils.cos((float) value);
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
}
