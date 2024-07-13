package moe.plushie.armourers_workshop.core.skin.molang.function.generic;

import moe.plushie.armourers_workshop.core.skin.molang.impl.MathHelper;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the arc-tangent theta of the input rectangular coordinate values (y,x), with the output converted to degrees
 */
public final class ATan2 extends Function {

    private final Expression y;
    private final Expression x;

    public ATan2(List<Expression> arguments) {
        super("math.atan2", 2, arguments);
        this.y = arguments.get(0);
        this.x = arguments.get(1);
    }

    @Override
    public double getAsDouble() {
        return Math.atan2(y.getAsDouble(), x.getAsDouble()) * MathHelper.RAD_TO_DEG;
    }
}
