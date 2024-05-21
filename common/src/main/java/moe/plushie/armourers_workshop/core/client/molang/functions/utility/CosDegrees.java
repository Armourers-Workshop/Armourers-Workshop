package moe.plushie.armourers_workshop.core.client.molang.functions.utility;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;
import moe.plushie.armourers_workshop.core.client.molang.functions.classic.Cos;

/**
 * Replacement function for {@link Cos}, operating in degrees rather than radians.
 */
public class CosDegrees extends Function {

    public CosDegrees(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.cos(this.getArg(0) / 180 * Math.PI);
    }
}
