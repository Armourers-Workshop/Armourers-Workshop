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
 * Returns the <a href="https://en.wikipedia.org/wiki/Hermite_polynomials">Hermite</a>> basis <code>3t^2 - 2t^3</code> curve interpolation value based on the input value
 */
public final class HermiteBlend extends Function {

    private final Expression valueA;

    public HermiteBlend(String name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.valueA = arguments.get(0);
    }

    @Override
    public double getAsDouble() {
        final double value = valueA.getAsDouble();
        return (3 * value * value) - (2 * value * value * value);
    }
}
