package moe.plushie.armourers_workshop.core.skin.molang.core;


import java.util.List;
import java.util.StringJoiner;

/**
 * Computational function wrapping a {@link Expression}
 * <p>
 * Subclasses of this represent mathematical functions to be performed on a pre-defined number of input variables.
 */
public abstract class Function implements Expression {

    private final String name;
    private final List<Expression> arguments;

    protected Function(String name, int requirement, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
        if (arguments.size() < requirement) {
            throw new IllegalArgumentException(String.format("Function '%s' at least %s arguments. Only %s given!", name, requirement, arguments.size()));
        }
    }

    @Override
    public boolean isMutable() {
        for (var value : arguments) {
            if (value.isMutable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (var arg : arguments) {
            joiner.add(arg.toString());
        }
        return name + joiner;
    }

    /**
     * Factory interface for {@link Function}.
     * Functionally equivalent to <pre>{@code Function<MathValue[], MathFunction>}</pre> but with a more concise user-facing handle
     */
    @FunctionalInterface
    public interface Factory<T extends Function> {
        /**
         * Instantiate a new {@link Function} for the given input values
         */
        T create(List<Expression> arguments);
    }
}
