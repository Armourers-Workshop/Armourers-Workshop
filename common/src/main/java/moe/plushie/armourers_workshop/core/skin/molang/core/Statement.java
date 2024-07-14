package moe.plushie.armourers_workshop.core.skin.molang.core;


import moe.plushie.armourers_workshop.core.skin.molang.impl.FlowController;
import moe.plushie.armourers_workshop.core.skin.molang.impl.FlowControllable;
import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

/**
 * Statement expression implementation. Statement expressions
 * do not have children expressions, they just have a single
 * operation type.
 *
 * <p>Example statement expressions: {@code break}, {@code continue}</p>
 */
public final class Statement implements Expression, FlowControllable {

    private final Op op;
    private final FlowController controller;

    public Statement(Op op) {
        this.op = op;
        this.controller = FlowController.instruct();
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitStatement(this);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public double getAsDouble() {
        controller.setInterrupt(op.mode());
        return 0;
    }

    @Override
    public String toString() {
        return op.symbol();
    }

    @Override
    public FlowController controller() {
        return controller;
    }

    public Op op() {
        return op;
    }

    public enum Op {
        BREAK("break", FlowController.State.BREAK),
        CONTINUE("continue", FlowController.State.CONTINUE);

        private final FlowController.State mode;
        private final String symbol;

        Op(final String symbol, final FlowController.State mode) {
            this.mode = mode;
            this.symbol = symbol;
        }

        public FlowController.State mode() {
            return mode;
        }

        public String symbol() {
            return symbol;
        }
    }
}
