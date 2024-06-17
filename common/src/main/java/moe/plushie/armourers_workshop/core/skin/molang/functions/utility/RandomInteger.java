package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;
import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;

public class RandomInteger extends Function {

    public RandomInteger(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        double min = Math.ceil(getArg(0));
        double max = Math.floor(getArg(1));
        return Math.floor(Math.random() * (max - min) + min);
    }
}
