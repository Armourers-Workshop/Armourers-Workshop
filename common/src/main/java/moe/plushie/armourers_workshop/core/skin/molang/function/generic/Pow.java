package moe.plushie.armourers_workshop.core.skin.molang.function.generic;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the input value raised to the power of the second input value
 */
public final class Pow extends Function {

    private final Expression value;
    private final Expression power;

    public Pow(List<Expression> arguments) {
        super("math.pow", 2, arguments);
        this.value = arguments.get(0);
        this.power = arguments.get(1);
    }

    @Override
    public double getAsDouble() {
        return Math.pow(value.getAsDouble(), power.getAsDouble());
    }
}
