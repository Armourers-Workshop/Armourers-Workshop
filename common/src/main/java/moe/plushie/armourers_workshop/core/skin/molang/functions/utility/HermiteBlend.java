package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;

public class HermiteBlend extends Function.Pure {

    public HermiteBlend(IMathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        double min = Math.ceil(getArg(0));
        return Math.floor(3 * Math.pow(min, 2) - 2 * Math.pow(min, 3));
    }
}
