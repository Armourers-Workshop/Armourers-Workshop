package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

import java.util.List;

public class ModVersionFunction extends Function {

    private final Expression modId;

    public ModVersionFunction(Expression receiver, List<Expression> arguments) {
        super(receiver, 1, arguments);
        this.modId = arguments.get(0);
    }

    @Override
    public double compute(final ExecutionContext context) {
        return 0;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        var modId = this.modId.evaluate(context);
        var version = EnvironmentManager.getModVersion(modId.getAsString());
        if (version != null) {
            return Result.valueOf(version.toString());
        }
        return Result.NULL;
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
