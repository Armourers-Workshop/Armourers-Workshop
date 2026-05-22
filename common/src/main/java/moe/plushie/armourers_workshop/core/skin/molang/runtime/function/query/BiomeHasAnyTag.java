package moe.plushie.armourers_workshop.core.skin.molang.runtime.function.query;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.EntityFunction;

import java.util.List;

public class BiomeHasAnyTag extends EntityFunction {

    private final List<Expression> tags;

    public BiomeHasAnyTag(Expression name, List<Expression> arguments) {
        super(name, 1, arguments);
        this.tags = arguments;
    }

    @Override
    public double compute(final EntitySelector entity, final ExecutionContext context) {
        var biome = entity.biome();
        if (biome == null) {
            return 0;
        }
        for (var tag : this.tags) {
            if (biome.hasTag(tag.evaluate(context).stringValue())) {
                return 1;
            }
        }
        return 0; // missing match.
    }
}

