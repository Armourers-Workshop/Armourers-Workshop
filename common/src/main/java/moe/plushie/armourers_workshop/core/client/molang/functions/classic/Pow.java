package moe.plushie.armourers_workshop.core.client.molang.functions.classic;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;

public class Pow extends Function {

    public Pow(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        return Math.pow(this.getArg(0), this.getArg(1));
    }
}
