package moe.plushie.armourers_workshop.core.skin.molang.runtime.function;


import moe.plushie.armourers_workshop.core.skin.molang.core.ComputedResult;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

import java.util.List;
import java.util.StringJoiner;

/**
 * Computational function wrapping a {@link Expression}
 * <p>
 * Subclasses of this represent mathematical functions to be performed on a pre-defined number of input variables.
 */
public abstract class Function implements Expression, Optimizable {

    protected final Expression receiver;
    protected final List<Expression> arguments;

    protected Function(Expression receiver, int requirement, List<Expression> arguments) {
        this.receiver = receiver;
        this.arguments = arguments;
        if (arguments.size() < requirement) {
            throw new IllegalArgumentException(String.format("Function '%s' at least %s arguments. Only %s given!", receiver, requirement, arguments.size()));
        }
    }

    @Override
    public abstract double compute(final ExecutionContext context);

    @Override
    public Result evaluate(final ExecutionContext context) {
        return ComputedResult.valueOf(compute(context));
    }

    @Override
    public boolean isMutable() {
        for (var argument : arguments) {
            if (argument.isMutable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        var joiner = new StringJoiner(", ", "(", ")");
        for (var arg : arguments) {
            joiner.add(arg.toString());
        }
        return receiver.toString() + joiner;
    }

    public Expression receiver() {
        return receiver;
    }

    public List<Expression> arguments() {
        return arguments;
    }

    /**
     * Factory interface for {@link Function}.
     * Functionally equivalent to <pre>{@code Function<MathValue[], Function>}</pre> but with a more concise user-facing handle
     */
    @FunctionalInterface
    public interface Factory<T extends Function> {
        /**
         * Instantiate a new {@link Function} for the given input values
         */
        T create(Expression receiver, List<Expression> arguments);
    }
}
