package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.functions.classic.Sin;
import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;

/**
 * Replacement function for {@link Sin}, operating in degrees rather than radians
 */
public class SinDegrees extends Function.Pure {

    public SinDegrees(IMathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.sin(getArg(0) / 180 * Math.PI);
    }
}
