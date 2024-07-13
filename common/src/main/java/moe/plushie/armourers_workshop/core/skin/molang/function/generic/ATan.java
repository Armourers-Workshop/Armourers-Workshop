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
 * Returns the arc-tangent of the input value angle, with the input angle converted to radians
 */
public final class ATan extends Function {

    private final Expression value;

    public ATan(List<Expression> arguments) {
        super("math.atan", 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double getAsDouble() {
        return Math.atan(value.getAsDouble() * MathHelper.DEG_TO_RAD);
    }
}
