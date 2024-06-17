package moe.plushie.armourers_workshop.core.skin.molang.functions.rounding;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

public class Trunc extends Function.Pure {

    public Trunc(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        double value = getArg(0);
        return value < 0 ? Math.ceil(value) : Math.floor(value);
    }
}
