package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;
import moe.plushie.armourers_workshop.core.skin.molang.math.Interpolations;

public class LerpRotate extends Function.Pure {

    public LerpRotate(IMathValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return Interpolations.lerpYaw(getArg(0), getArg(1), getArg(2));
    }
}
