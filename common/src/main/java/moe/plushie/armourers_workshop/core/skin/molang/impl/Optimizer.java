package moe.plushie.armourers_workshop.core.skin.molang.impl;

import moe.plushie.armourers_workshop.core.skin.molang.core.Binary;
import moe.plushie.armourers_workshop.core.skin.molang.core.Compound;
import moe.plushie.armourers_workshop.core.skin.molang.core.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimized;
import moe.plushie.armourers_workshop.core.skin.molang.core.Subscript;
import moe.plushie.armourers_workshop.core.skin.molang.core.Ternary;
import moe.plushie.armourers_workshop.core.skin.molang.core.Unary;

import java.util.ArrayList;
import java.util.List;

public final class Optimizer implements Visitor {

    private final Compiler compiler;

    public Optimizer(Compiler compiler) {
        this.compiler = compiler;
    }

    public Expression optimize(Expression expression) {
        // when an expression is mutable, we will try to optimize its arguments.
        if (expression.isMutable()) {
            return expression.visit(this);
        }
        // we don't need to wrap the constant expression.
        if (expression instanceof Constant) {
            return expression;
        }
        return new Optimized(expression);
    }

    public List<Expression> optimize(List<Expression> expressions) {
        var results = new ArrayList<Expression>();
        for (var expression : expressions) {
            results.add(optimize(expression));
        }
        return results;
    }

    @Override
    public Expression visitSubscript(final Subscript expression) {
        return new Subscript(expression.variable(), optimize(expression.index()));
    }

    @Override
    public Expression visitUnary(final Unary expression) {
        return new Unary(expression.op(), optimize(expression.value()));
    }

    @Override
    public Expression visitBinary(final Binary expression) {
        return new Binary(expression.op(), optimize(expression.left()), optimize(expression.right()));
    }

    @Override
    public Expression visitTernary(final Ternary expression) {
        return new Ternary(optimize(expression.condition()), optimize(expression.trueValue()), optimize(expression.falseValue()));
    }

    @Override
    public Expression visitFunction(final Function expression) {
        var result = compiler.getFunction(expression.name(), optimize(expression.arguments()));
        if (result != null) {
            return result;
        }
        return expression;
    }

    @Override
    public Expression visitCompound(final Compound expression) {
        return new Compound(optimize(expression.expressions()));
    }
}
