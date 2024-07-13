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
 * Returns the square root of the input value
 */
public final class Sqrt extends Function {

    private final Expression value;

    public Sqrt(List<Expression> arguments) {
        super("math.sqrt", 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double getAsDouble() {
        return Math.sqrt(value.getAsDouble());
    }
}
