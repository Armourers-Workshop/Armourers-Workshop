package moe.plushie.armourers_workshop.core.skin.molang.core;

import java.util.List;

// for_each(<variable>, <array>, <expression>);
public final class ForEach extends Function {

    private final Expression variable;
    private final Expression array;
    private final Expression expression;

    public ForEach(String name, List<Expression> arguments) {
        super(name, 3, arguments);
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

    public Expression variable() {
        return variable;
    }

    public Expression array() {
        return array;
    }

    public Expression expression() {
        return expression;
    }
}
