package moe.plushie.armourers_workshop.core.skin.molang.functions.limit;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

public class Min extends Function.Pure {

    public Min(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        return Math.min(getArg(0), getArg(1));
    }
}
