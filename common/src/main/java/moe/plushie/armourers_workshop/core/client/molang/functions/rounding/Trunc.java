package moe.plushie.armourers_workshop.core.client.molang.functions.rounding;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;

public class Trunc extends Function {

    public Trunc(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        double value = this.getArg(0);

        return value < 0 ? Math.ceil(value) : Math.floor(value);
    }
}
