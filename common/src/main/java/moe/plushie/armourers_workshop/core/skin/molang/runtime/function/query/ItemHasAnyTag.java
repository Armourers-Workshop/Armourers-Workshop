package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.LivingEntityFunction;

import java.util.List;
import java.util.stream.Collectors;

public class ItemHasAnyTag extends LivingEntityFunction {

    private final Expression slot;
    private final List<Expression> tags;

    public ItemHasAnyTag(Expression name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.slot = arguments.get(0);
        this.tags = arguments.stream().skip(1).collect(Collectors.toList());
    }

    @Override
    public double compute(final LivingEntitySelector entity, final ExecutionContext context) {
        var item = entity.equipmentBySlot(this.slot.evaluate(context).getAsString());
        if (item == null) {
            return 0; // can't found item.
        }
        for (var tag : this.tags) {
            if (item.hasTag(tag.evaluate(context).getAsString())) {
                return 1;
            }
        }
        return 0; // missing match.
    }
}
