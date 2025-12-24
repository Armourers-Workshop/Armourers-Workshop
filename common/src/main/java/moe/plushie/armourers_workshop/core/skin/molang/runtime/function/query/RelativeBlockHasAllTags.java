package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.EntityFunction;

import java.util.List;
import java.util.stream.Collectors;

public class RelativeBlockHasAllTags extends EntityFunction {

    private final Expression offsetX;
    private final Expression offsetY;
    private final Expression offsetZ;
    private final List<Expression> tags;

    public RelativeBlockHasAllTags(Expression name, List<Expression> arguments) {
        super(name, 4, arguments);
        this.offsetX = arguments.get(0);
        this.offsetY = arguments.get(1);
        this.offsetZ = arguments.get(2);
        this.tags = arguments.stream().skip(3).collect(Collectors.toList());
    }

    @Override
    public double compute(final EntitySelector entity, final ExecutionContext context) {
        var offsetX = this.offsetX.evaluate(context).getAsInt();
        var offsetY = this.offsetY.evaluate(context).getAsInt();
        var offsetZ = this.offsetZ.evaluate(context).getAsInt();
        // query limit
        if (Math.abs(offsetX) > 8 || Math.abs(offsetY) > 8 || Math.abs(offsetZ) > 8) {
            return 0; // too far
        }

        var block = entity.relativeBlock(offsetX, offsetY, offsetZ);
        if (block == null) {
            return 0; // can't found
        }

        for (var tag : this.tags) {
            if (!block.hasTag(tag.evaluate(context).getAsString())) {
                return 0; // missing match.
            }
        }
        return 1;
    }
}

