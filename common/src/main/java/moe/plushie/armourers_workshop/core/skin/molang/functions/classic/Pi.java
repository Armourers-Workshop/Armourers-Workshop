package moe.plushie.armourers_workshop.core.skin.molang.functions.classic;

import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

public class Pi extends Function.Pure {

    public Pi(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public double get() {
        return Math.PI;
    }
}
