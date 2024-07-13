package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.MathHelper;

import java.util.List;

// loop(<count>, <expression>);
public final class Loop extends Function {

    private final Expression count;
    private final Expression expression;

    public Loop(String name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.count = arguments.get(0);
        this.expression = arguments.get(1);
    }

    @Override
    public double getAsDouble() {
        int total = MathHelper.floor(count.getAsDouble());
        double result = 0;
        for (int i = 0; i < total; i++) {
            result = expression.getAsDouble();
        }
        return 0;
    }

    public Expression count() {
        return count;
    }

    public Expression expression() {
        return expression;
    }
}
