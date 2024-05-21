package moe.plushie.armourers_workshop.core.client.molang.functions.utility;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;

public class HermiteBlend extends Function {

    public java.util.Random random;

    public HermiteBlend(IValue[] values, String name) throws Exception {
        super(values, name);

        this.random = new java.util.Random();
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        double min = Math.ceil(this.getArg(0));
        return Math.floor(3 * Math.pow(min, 2) - 2 * Math.pow(min, 3));
    }
}
