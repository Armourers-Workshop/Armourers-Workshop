package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.builtin;

import moe.plushie.armourers_workshop.core.skin.molang.core.Assignable;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.List;

// Parameters:
// - any:              Variable
// - array:            Any array
// - CallableBinding:  The looped expressions

// for_each(<variable>, <array>, <expression>);
public final class ForEach extends Function {

    private final Expression var;
    private final Expression array;
    private final Expression body;

    public ForEach(Expression name, List<Expression> arguments) {
        super(name, 3, arguments);
        this.var = arguments.get(0);
        this.array = arguments.get(1);
        this.body = arguments.get(2);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return evaluate(context).doubleValue();
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        // first argument must be an access expression,
        // e.g. 'variable.test', 'v.pig', 't.entity' or 't.entity.location.world'
        if (!(var instanceof Assignable cur)) {
            ModLog.warn("Cannot assign a value to {}", var);
            return Result.NULL;
        }
        var elements = array.evaluate(context);
        var scope = context.stack().scope().beginEnumerate();
        for (int i = 0, size = elements.size(); i < size; i++) {
            cur.assign(elements.get(i), context);
            body.evaluate(context);
            if (scope.isBreakOrReturn()) {
                break;
            }
        }
        return scope.endEnumerate();
    }

    public Expression receiver() {
        return var;
    }

    public Expression array() {
        return array;
    }

    public Expression body() {
        return body;
    }
}
