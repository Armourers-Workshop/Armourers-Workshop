package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.EntityFunction;

import java.util.List;

public class Position extends EntityFunction {

    private final Expression axis;

    public Position(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.axis = arguments.get(0);
    }

    @Override
    public double compute(final EntitySelector entity, final ExecutionContext context) {
        var axis = this.axis.evaluate(context).intValue();
        var partialTick = entity.partialTick();
        return switch (axis) {
            case 0 -> entity.getX(partialTick);
            case 1 -> entity.getY(partialTick);
            case 2 -> entity.getZ(partialTick);
            default -> 0;
        };
    }
}
