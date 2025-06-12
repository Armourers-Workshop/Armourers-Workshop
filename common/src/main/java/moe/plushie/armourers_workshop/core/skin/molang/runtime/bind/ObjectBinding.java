package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;

public interface ObjectBinding extends Expression {

    Expression propertyByName(String name);

    @Override
    default Result evaluate(final ExecutionContext context) {
        return Result.NULL;
    }
}
