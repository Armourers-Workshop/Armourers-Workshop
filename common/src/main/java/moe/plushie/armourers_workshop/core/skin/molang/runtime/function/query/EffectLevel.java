package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.LivingEntityFunction;

import java.util.List;

public class EffectLevel extends LivingEntityFunction {

    private final Expression effect;

    public EffectLevel(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.effect = arguments.get(0);
    }

    @Override
    public double compute(final LivingEntitySelector entity, final ExecutionContext context) {
        var effect = entity.effectByName(this.effect.evaluate(context).stringValue());
        if (effect != null) {
            return effect.level();
        }
        return 0;
    }
}
