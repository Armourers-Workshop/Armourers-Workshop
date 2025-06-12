package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.variable;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ContextSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LevelSelector;

@FunctionalInterface
public interface LevelVariableBinding extends LambdaVariableBinding {

    Object apply(final LevelSelector entity);

    @Override
    default Result evaluate(final ExecutionContext context) {
        if (context instanceof ContextSelector context1) {
            var level = context1.level();
            if (level != null) {
                var result = apply(level);
                return Result.parse(result);
            }
        }
        return Result.NULL;
    }
}
