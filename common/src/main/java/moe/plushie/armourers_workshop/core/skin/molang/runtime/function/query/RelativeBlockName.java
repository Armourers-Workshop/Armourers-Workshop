package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.EntityFunction;

import java.util.List;

public class RelativeBlockName extends EntityFunction {

    private final Expression offsetX;
    private final Expression offsetY;
    private final Expression offsetZ;

    public RelativeBlockName(Expression name, List<Expression> arguments) {
        super(name, 3, arguments);
        this.offsetX = arguments.get(0);
        this.offsetY = arguments.get(1);
        this.offsetZ = arguments.get(2);
    }

    @Override
    public double compute(final EntitySelector entity, final ExecutionContext context) {
        return 0;
    }

    @Override
    public Result evaluate(EntitySelector entity, ExecutionContext context) {
        var offsetX = this.offsetX.evaluate(context).getAsInt();
        var offsetY = this.offsetY.evaluate(context).getAsInt();
        var offsetZ = this.offsetZ.evaluate(context).getAsInt();
        // query limit
        if (Math.abs(offsetX) > 8 || Math.abs(offsetY) > 8 || Math.abs(offsetZ) > 8) {
            return Result.NULL; // too far
        }
        var block = entity.relativeBlock(offsetX, offsetY, offsetZ);
        if (block == null) {
            return Result.NULL; // can't found.
        }
        return Result.valueOf(block.id());
    }
}
