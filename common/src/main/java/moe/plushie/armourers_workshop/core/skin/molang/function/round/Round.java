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
 * Returns the closest integer value to the input value
 */
public final class Round extends Function {

    private final Expression value;

    public Round(List<Expression> arguments) {
        super("math.round", 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double getAsDouble() {
        return Math.round(value.getAsDouble());
    }
}
