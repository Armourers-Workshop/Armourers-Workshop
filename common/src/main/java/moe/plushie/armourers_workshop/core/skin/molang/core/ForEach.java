package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.FlowControllable;
import moe.plushie.armourers_workshop.core.skin.molang.impl.FlowController;
import moe.plushie.armourers_workshop.core.skin.molang.impl.Property;

import java.util.Collections;
import java.util.List;

// for_each(<variable>, <array>, <expression>);
public final class ForEach extends Function implements FlowControllable {

    private final Expression variable;
    private final Expression array;
    private final Expression body;
    private final FlowController controller;

    public ForEach(String name, List<Expression> arguments) {
        super(name, 3, arguments);
        this.variable = arguments.get(0);
        this.array = arguments.get(1);
        this.body = arguments.get(2);
        this.controller = FlowController.enumerate(body);
    }

    @Override
    public double getAsDouble() {
        return getAsExpression().getAsDouble();
    }

    @Override
    public Expression getAsExpression() {
        controller.begin();
        var entries = getEntries();
        for (var entry : entries) {
            if (variable instanceof Property property) {
                property.update(entry.getAsExpression());
            }
            body.getAsDouble();
            if (controller.interrupt().isBreakOrReturn()) {
                break;
            }
        }
        return controller.end();
    }

    @Override
    public FlowController controller() {
        return controller;
    }

    public Expression variable() {
        return variable;
    }

    public Expression array() {
        return array;
    }

    public Expression body() {
        return body;
    }

    private List<Expression> getEntries() {
        // TODO: query multiple object from an expression.
        var expr = array.getAsExpression();
        //var entries = array.getAsExpression();
        return Collections.emptyList();
    }
}
