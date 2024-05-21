package moe.plushie.armourers_workshop.core.client.molang.functions.limit;

import moe.plushie.armourers_workshop.core.client.molang.functions.Function;
import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.math.MathHelper;

public class Clamp extends Function {

    public Clamp(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return MathHelper.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
