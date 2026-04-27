package moe.plushie.armourers_workshop.compat.core.math;

import moe.plushie.armourers_workshop.api.annotation.Available;

@Available("[18, )")
public class AbstractMath {

    public static final boolean HAS_FAST_MATH = true;

    public static float fma(float a, float b, float c) {
        return Math.fma(a, b, c);
    }

    public static double fma(double a, double b, double c) {
        return Math.fma(a, b, c);
    }
}
