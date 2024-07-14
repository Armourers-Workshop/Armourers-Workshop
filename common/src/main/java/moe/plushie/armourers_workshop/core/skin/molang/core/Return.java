package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.FlowController;
import moe.plushie.armourers_workshop.core.skin.molang.impl.FlowControllable;
import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * The following statement and stops execution of the expression, returns the value computed.
 */
public final class Return implements Expression, FlowControllable {

    private final Expression value;
    private final FlowController controller;

    public Return(Expression value) {
        this.value = value;
        this.controller = FlowController.instruct();
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitReturn(this);
    }

    @Override
    public boolean isMutable() {
        return value.isMutable();
    }

    @Override
    public double getAsDouble() {
        var result = value.getAsExpression();
        controller.setInterrupt(FlowController.State.RETURN, result);
        return 0;
    }

    @Override
    public String toString() {
        return "return " + value;
    }

    @Override
    public FlowController controller() {
        return controller;
    }

    public Expression value() {
        return value;
    }
}
