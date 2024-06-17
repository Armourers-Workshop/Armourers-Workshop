package moe.plushie.armourers_workshop.core.skin.molang.functions.utility;

import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;
import moe.plushie.armourers_workshop.core.skin.molang.functions.Function;

public class Random extends Function {

    public java.util.Random random;

    public Random(IValue[] values, String name) throws Exception {
        super(values, name);
        this.random = new java.util.Random();
    }

    @Override
    public double get() {
        double random = 0;

        if (this.args.length >= 3) {
            this.random.setSeed((long) getArg(2));
            random = this.random.nextDouble();
        } else {
            random = Math.random();
        }

        if (this.args.length >= 2) {
            double a = getArg(0);
            double b = getArg(1);

            double min = Math.min(a, b);
            double max = Math.max(a, b);

            random = random * (max - min) + min;
        } else if (this.args.length >= 1) {
            random = random * getArg(0);
        }

        return random;
    }
}
