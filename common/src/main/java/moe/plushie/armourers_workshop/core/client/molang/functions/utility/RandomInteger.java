package moe.plushie.armourers_workshop.core.client.molang.functions.utility;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;

public class RandomInteger extends Function {

    public java.util.Random random;

    public RandomInteger(IValue[] values, String name) throws Exception {
        super(values, name);

        this.random = new java.util.Random();
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        double min = Math.ceil(this.getArg(0));
        double max = Math.floor(this.getArg(1));
        return Math.floor(Math.random() * (max - min) + min);
    }
}
