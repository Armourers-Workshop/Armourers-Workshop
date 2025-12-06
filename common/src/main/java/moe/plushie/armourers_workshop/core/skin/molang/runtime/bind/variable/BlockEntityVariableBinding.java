package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.BlockEntitySelector;

@FunctionalInterface
public interface BlockEntityVariableBinding extends LambdaVariableBinding {

    Object apply(final BlockEntitySelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context.entity() instanceof BlockEntitySelector entity) {
            var result = apply(entity);
            return Result.parse(result);
        }
        return Result.NULL;
    }
}
