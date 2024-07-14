package moe.plushie.armourers_workshop.core.skin.molang.core;


import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * An optimized expression result value.
 */
public final class Optimized implements Expression {

    private final double value;
    private final Expression expression;

    public Optimized(Expression expression) {
        this.value = expression.getAsDouble();
        this.expression = expression;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return value + "/*" + expression + "*/";
    }

    public Expression expression() {
        return expression;
    }
}
