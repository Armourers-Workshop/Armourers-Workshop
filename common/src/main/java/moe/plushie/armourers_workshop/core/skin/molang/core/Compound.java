package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

import java.util.List;
import java.util.StringJoiner;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Contains a collection of sub-expressions that evaluate before returning the last expression, or 0 if no return is defined.
 * Sub-expressions have no bearing on the final return with exception for where they may be setting variable values
 */
public final class Compound implements Expression {

    private final List<Expression> expressions;

    public Compound(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitCompound(this);
    }

    @Override
    public boolean isMutable() {
        for (var expression : expressions) {
            if (expression.isMutable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getAsDouble() {
        double result = 0;
        for (var expression : expressions) {
            // break;
            // continue;
            // return i;
            result = expression.getAsDouble();
        }
        return result;
    }

    @Override
    public String toString() {
        final var joiner = new StringJoiner("; ", "{", "}");
        for (var expr : expressions) {
            joiner.add(expr.toString());
        }
        return joiner.toString();
    }

    public List<Expression> expressions() {
        return expressions;
    }
}
