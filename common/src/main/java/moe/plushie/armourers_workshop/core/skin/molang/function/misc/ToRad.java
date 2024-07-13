package moe.plushie.armourers_workshop.core.skin.molang.function.misc;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Converts the input value to radians
 */
public final class ToRad extends Function {

    private final Expression value;

    public ToRad(List<Expression> arguments) {
        super("math.to_rad", 1, arguments);
        this.value = arguments.get(0);
    }

    @Override
    public double getAsDouble() {
        return Math.toRadians(value.getAsDouble());
    }
}
