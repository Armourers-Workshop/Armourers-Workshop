package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.functions.classic.Cos;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

/**
 * Replacement function for {@link Cos}, operating in degrees rather than radians.
 */
public class CosDegrees extends Function.Pure {

    public CosDegrees(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.cos(getArg(0) / 180 * Math.PI);
    }
}
