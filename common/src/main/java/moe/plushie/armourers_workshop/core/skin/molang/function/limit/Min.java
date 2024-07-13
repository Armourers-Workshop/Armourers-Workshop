package moe.plushie.armourers_workshop.core.skin.molang.function.limit;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;

import java.util.List;

/**
 * {@link Function} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the lesser of the two input values
 */
public final class Min extends Function {

    private final Expression valueA;
    private final Expression valueB;

    public Min(String name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.valueA = arguments.get(0);
        this.valueB = arguments.get(1);
    }

    @Override
    public double getAsDouble() {
        return Math.min(valueA.getAsDouble(), valueB.getAsDouble());
    }
}
