package moe.plushie.armourers_workshop.core.skin.molang.function.round;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the smallest value that is greater than or equal to the input value and is equal to an integer
 */
public final class Ceil extends Function {

    private final Expression value;

    public Ceil(List<Expression> arguments) {
        super("math.ceil", 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double getAsDouble() {
        return Math.ceil(value.getAsDouble());
    }
}
