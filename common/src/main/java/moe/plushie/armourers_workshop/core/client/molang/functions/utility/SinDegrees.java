package moe.plushie.armourers_workshop.core.client.molang.functions.utility;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;
import moe.plushie.armourers_workshop.core.client.molang.functions.classic.Sin;

/**
 * Replacement function for {@link Sin}, operating in degrees rather than radians
 */
public class SinDegrees extends Function {

    public SinDegrees(IValue[] values, String name) throws Exception {
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
