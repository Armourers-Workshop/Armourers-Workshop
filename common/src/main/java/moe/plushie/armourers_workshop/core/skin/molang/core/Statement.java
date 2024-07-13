package moe.plushie.armourers_workshop.core.skin.molang.core;


import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

/**
 * Statement expression implementation. Statement expressions
 * do not have children expressions, they just have a single
 * operation type.
 *
 * <p>Example statement expressions: {@code break}, {@code continue}</p>
 */
public final class Statement implements Expression {

    private final Op op;

    public Statement(Op op) {
        this.op = op;
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
        return 0;
    }

    @Override
    public String toString() {
        return op.symbol();
    }

    public Op op() {
        return op;
    }

    public enum Op {
        BREAK("break"),
        CONTINUE("continue");

        private final String symbol;

        Op(final String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }
}
