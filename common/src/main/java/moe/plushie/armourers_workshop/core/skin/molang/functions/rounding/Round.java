package moe.plushie.armourers_workshop.core.skin.molang.functions.rounding;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;

public class Round extends Function.Pure {

    public Round(IMathValue[] values, String name) throws Exception {
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
