package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

public class DieRollInteger extends Function {

    public DieRollInteger(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        double i = 0;
        double total = 0;
        while (i < getArg(0)) {
            total += Math.round(getArg(1) + Math.random() * (getArg(2) - getArg(1)));
        }
        return total;
    }
}
