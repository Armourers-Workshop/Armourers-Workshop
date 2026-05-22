package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.function;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.EntityFunction;

import java.util.List;

public class BoneAccessFunction extends EntityFunction {

    private final Expression name;

    public BoneAccessFunction(Expression receiver, List<Expression> arguments) {
        super(receiver, 1, arguments);
        this.name = arguments.get(0);
    }

    public static BoneAccessFunction position(Expression receiver, List<Expression> arguments) {
        return new BoneAccessFunction(receiver, arguments);
    }

    public static BoneAccessFunction scale(Expression receiver, List<Expression> arguments) {
        return new BoneAccessFunction(receiver, arguments);
    }

    public static BoneAccessFunction rotation(Expression receiver, List<Expression> arguments) {
        return new BoneAccessFunction(receiver, arguments);
    }

    public static BoneAccessFunction pivot(Expression receiver, List<Expression> arguments) {
        return new BoneAccessFunction(receiver, arguments);
    }


    @Override
    public double compute(final EntitySelector entity, final ExecutionContext context) {
        return 0;
    }

    @Override
    public Result evaluate(EntitySelector entity, ExecutionContext context) {
        var name = this.name.evaluate(context).stringValue();
//        var bone = context.entity().geoInstance().getAnimationProcessor().getBone(str);
//        if (bone == null) {
//            return null;
//        }
//        return getParam(bone);
        return Result.NULL;
    }
}
