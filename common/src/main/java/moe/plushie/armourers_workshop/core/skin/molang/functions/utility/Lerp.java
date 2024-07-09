package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;
import moe.plushie.armourers_workshop.core.skin.molang.math.Interpolations;

public class Lerp extends Function.Pure {

    public Lerp(IMathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return Interpolations.lerp(getArg(0), getArg(1), getArg(2));
    }
}
