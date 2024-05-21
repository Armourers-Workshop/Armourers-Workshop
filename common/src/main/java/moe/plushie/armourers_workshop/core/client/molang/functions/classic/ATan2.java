package moe.plushie.armourers_workshop.core.client.molang.functions.classic;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;

/**
 * Absolute value function
 */
public class ATan2 extends Function {

    public ATan2(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        return Math.atan2(this.getArg(0), this.getArg(1));
    }
}
