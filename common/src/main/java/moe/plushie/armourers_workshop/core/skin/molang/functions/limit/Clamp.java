package moe.plushie.armourers_workshop.core.skin.molang.functions.limit;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;
import moe.plushie.armourers_workshop.core.skin.molang.math.MathHelper;

public class Clamp extends Function.Pure {

    public Clamp(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return MathHelper.clamp(getArg(0), getArg(1), getArg(2));
    }
}
