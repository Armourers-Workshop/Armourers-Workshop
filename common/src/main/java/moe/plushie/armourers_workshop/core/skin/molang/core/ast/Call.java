package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;

import java.util.List;
import java.util.StringJoiner;

/**
 * Call expression implementation, executes function
 * with certain arguments.
 *
 * <p>Example call expressions: {@code print('hello')},
 * {@code math.sqrt(9)}, {@code math.pow(3, 2)}</p>
 */
public final class Call implements Expression, Optimizable {

    private final Expression receiver;
    private final List<Expression> arguments;

    public Call(Expression receiver, List<Expression> arguments) {
        this.receiver = receiver;
        this.arguments = arguments;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        // we will create a temporary object to process call.
        if (receiver instanceof Function.Factory<?> factory) {
            return factory.create(receiver, arguments).evaluate(context);
        }
        return receiver.evaluate(context);
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitCall(this);
    }

    @Override
    public boolean isMutable() {
        // we will create a temporary object to check function is mutable?
        if (receiver instanceof Function.Factory<?> factory) {
            return factory.create(receiver, arguments).isMutable();
        }
        for (var argument : arguments) {
            if (argument.isMutable()) {
                return true;
            }
        }
        return receiver.isMutable();
    }

    @Override
    public String toString() {
        var joiner = new StringJoiner(", ");
        for (var arg : arguments) {
            joiner.add(arg.toString());
        }
        return String.format("%s.call(%s)", receiver.toString(), joiner);
    }

    public Expression receiver() {
        return receiver;
    }

    public List<Expression> arguments() {
        return arguments;
    }
}
