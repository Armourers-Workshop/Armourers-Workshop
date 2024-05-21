package moe.plushie.armourers_workshop.core.client.molang.functions.limit;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;

public class Max extends Function {

    public Max(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        return Math.max(this.getArg(0), this.getArg(1));
    }
}
