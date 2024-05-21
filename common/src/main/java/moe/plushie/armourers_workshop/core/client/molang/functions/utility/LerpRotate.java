package moe.plushie.armourers_workshop.core.client.molang.functions.utility;

import moe.plushie.armourers_workshop.core.client.molang.math.IValue;
import moe.plushie.armourers_workshop.core.client.molang.functions.Function;
import moe.plushie.armourers_workshop.core.client.molang.math.Interpolations;

public class LerpRotate extends Function {

    public LerpRotate(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return Interpolations.lerpYaw(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
