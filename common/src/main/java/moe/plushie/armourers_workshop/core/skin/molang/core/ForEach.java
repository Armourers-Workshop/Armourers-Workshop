package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.MathHelper;

import java.util.List;

// for_each(<variable>, <array>, <expression>);
public final class ForEach extends Function{

    private final Expression variable;
    private final Expression array;
    private final Expression expression;

    public ForEach(List<Expression> arguments) {
        super("for_each", 3, arguments);
        this.variable = arguments.get(0);
        this.array = arguments.get(1);
        this.expression = arguments.get(2);
    }

    @Override
    public double getAsDouble() {
//        int total = MathHelper.floor(count.getAsDouble());
//        double result = 0;
//        for (int i = 0; i < total; i++) {
//            result = expression.getAsDouble();
//        }
        return 0;
    }
}
