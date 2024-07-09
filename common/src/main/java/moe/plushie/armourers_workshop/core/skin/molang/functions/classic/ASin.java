package moe.plushie.armourers_workshop.core.skin.molang.functions.classic;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;

/**
 * Absolute value function
 */
public class ASin extends Function.Pure {

    public ASin(IMathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.asin(getArg(0));
    }
}
