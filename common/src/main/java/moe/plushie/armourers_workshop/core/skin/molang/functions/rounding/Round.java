package moe.plushie.armourers_workshop.core.skin.molang.functions.rounding;

import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;
import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;

public class Round extends Function.Pure {

    public Round(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.round(getArg(0));
    }
}
