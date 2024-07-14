package moe.plushie.armourers_workshop.core.skin.molang.impl;

import moe.plushie.armourers_workshop.core.skin.molang.core.Binary;
import moe.plushie.armourers_workshop.core.skin.molang.core.Compound;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;
import moe.plushie.armourers_workshop.core.skin.molang.core.Return;
import moe.plushie.armourers_workshop.core.skin.molang.core.Subscript;
import moe.plushie.armourers_workshop.core.skin.molang.core.Ternary;
import moe.plushie.armourers_workshop.core.skin.molang.core.Unary;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class TreeVisitor implements Visitor {

    public static List<Expression> filter(Expression expr, Predicate<Expression> filter) {
        var results = new ArrayList<Expression>();
        expr.visit(new TreeVisitor() {
            @Override
            public Expression visit(Expression expression) {
                if (filter.test(expression)) {
                    results.add(expression);
                }
                return expression;
            }
        });
        return results;
    }

    @Override
    public Expression visitSubscript(final Subscript expression) {
        expression.variable().visit(this);
        expression.index().visit(this);
        return visit(expression);
    }

    @Override
    public Expression visitUnary(final Unary expression) {
        expression.value().visit(this);
        return visit(expression);
    }

    @Override
    public Expression visitBinary(final Binary expression) {
        expression.left().visit(this);
        expression.right().visit(this);
        return visit(expression);
    }

    @Override
    public Expression visitTernary(final Ternary expression) {
        expression.condition().visit(this);
        expression.trueValue().visit(this);
        expression.falseValue().visit(this);
        return visit(expression);
    }

    @Override
    public Expression visitFunction(final Function expression) {
        for (var argument : expression.arguments()) {
            argument.visit(this);
        }
        return visit(expression);
    }

    @Override
    public Expression visitCompound(final Compound expression) {
        for (var argument : expression.expressions()) {
            argument.visit(this);
        }
        return visit(expression);
    }

    @Override
    public Expression visitReturn(final Return expression) {
        expression.value().visit(this);
        return visit(expression);
    }
}
