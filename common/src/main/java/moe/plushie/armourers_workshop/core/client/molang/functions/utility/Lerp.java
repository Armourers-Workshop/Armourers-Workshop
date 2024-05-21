package moe.plushie.armourers_workshop.core.client.molang.functions.utility;

import moe.plushie.armourers_workshop.core.client.molang.functions.Function;
import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.math.Interpolations;

public class Lerp extends Function {

    public Lerp(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return Interpolations.lerp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
