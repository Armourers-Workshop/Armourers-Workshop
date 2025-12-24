package moe.plushie.armourers_workshop.core.skin.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Binary;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Call;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Optimized;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.StructAccess;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;

public final class Optimizer extends Transformer {

    @Override
    public Expression transform(Expression expression) {
        // when an expression is mutable, we will try to optimize its arguments.
        if (expression.isMutable()) {
            return expression.visit(this);
        }
        // we can't wrap the constant expression.
        if (expression instanceof Optimizable) {
            return new Optimized(expression.evaluate(OptimizeContext.DEFAULT), expression);
        }
        return expression;
    }

    @Override
    public Expression visitCall(Call expression) {
        // optimized to direct function calls.
        if (expression.receiver() instanceof Function.Factory<?> factory) {
            var arguments = transform(expression.arguments());
            return factory.create(expression.receiver(), arguments);
        }
        // a.b.c().f1()
        // a.b.c[0].f1()
        if (expression.receiver() instanceof StructAccess access) {
            // TODO: @SAGESSE NO IMPL
            //return new Call(transform(access), transform(expression.arguments()));
        }
        return super.visitCall(expression);
    }

    @Override
    public Expression visitBinary(Binary expression) {
        // a ^ n: a * a * a
        // a * n: a + a + a
        // a op= b: a = a op b, op: + - * / % ^ ??
        return super.visitBinary(expression);
    }

}
