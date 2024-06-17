package moe.plushie.armourers_workshop.core.skin.molang.functions.classic;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

public class Cos extends Function.Pure {

    public Cos(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.cos(getArg(0));
    }
}
