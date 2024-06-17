package moe.plushie.armourers_workshop.core.skin.molang.functions.rounding;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

public class Ceil extends Function.Pure {

    public Ceil(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.ceil(getArg(0));
    }
}
