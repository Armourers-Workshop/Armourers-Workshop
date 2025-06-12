package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function;

import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.BlockEntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.EntitySelectorImpl;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class PlayAnimationFunction extends Function {

    private final Expression name;

    public PlayAnimationFunction(Expression receiver, List<Expression> arguments) {
        super(receiver, 1, arguments);
        this.name = arguments.get(0);
    }

    @Override
    public double compute(ExecutionContext context) {
        var target = getTarget(context);
        if (target == null) {
            return 0; // can't found target.
        }
        var name = this.name.evaluate(context).getAsString();
        if (name.isEmpty()) {
            return 0;
        }
        target.play(name, TickUtils.animationTicks(), new CompoundTag());
        return 0;
    }

    private AnimationManager getTarget(ExecutionContext context) {
        if (context instanceof EntitySelectorImpl<?> entity) {
            return AnimationManager.of(entity.entity());
        }
        if (context instanceof BlockEntitySelectorImpl<?> entity) {
            return AnimationManager.of(entity.getEntity());
        }
        return null;
    }
}
