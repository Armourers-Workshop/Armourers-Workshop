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
 * Returns <a href="https://en.wikipedia.org/wiki/Pi">PI</a>
 */
public final class Pi extends Function {

    public Pi(List<Expression> arguments) {
        super("math.pi", 0, arguments);
    }

    @Override
    public double getAsDouble() {
        return Math.PI;
    }
}
