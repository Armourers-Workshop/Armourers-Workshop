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
 * Returns the remainder value of the input value when modulo'd by the modulus value
 */
public final class Mod extends Function {

    private final Expression value;
    private final Expression modulus;

    public Mod(String name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.value = arguments.get(0);
        this.modulus = arguments.get(1);
    }

    @Override
    public double getAsDouble() {
        return value.getAsDouble() % modulus.getAsDouble();
    }
}
