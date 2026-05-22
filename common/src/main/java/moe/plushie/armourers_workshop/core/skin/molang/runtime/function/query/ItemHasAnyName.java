package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.LivingEntityFunction;

import java.util.List;
import java.util.stream.Collectors;

public class ItemHasAnyName extends LivingEntityFunction {

    private final Expression slot;
    private final List<Expression> names;

    public ItemHasAnyName(Expression name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.slot = arguments.get(0);
        this.names = arguments.stream().skip(1).collect(Collectors.toList());
    }

    @Override
    public double compute(final LivingEntitySelector entity, final ExecutionContext context) {
        var item = entity.equipmentBySlot(this.slot.evaluate(context).stringValue());
        if (item == null) {
            return 0; // can't found item.
        }
        var actualId = item.id();
        for (var name : this.names) {
            var id = name.evaluate(context).stringValue();
            if (!id.isEmpty() && !id.contains(":")) {
                id = "minecraft:" + id;
            }
            if (actualId.equals(id)) {
                return 1; // found
            }
        }
        return 0; // can't found.
    }
}
