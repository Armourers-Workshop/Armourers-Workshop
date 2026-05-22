package moe.plushie.armourers_workshop.core.skin.molang.core.ast;

import moe.plushie.armourers_workshop.core.skin.molang.core.Assignable;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Optimizable;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.Visitor;

import java.util.function.Function;

/**
 * Array accessing expression implementation, access to a value in
 * an array, by its index.
 *
 * <p>Example array accessing expressions: {@code my_geometries[0]},
 * {@code array.my_geometries[query.anim_time]}, {@code array.my_geos[math.cos(90)]}</p>
 */
public final class ArrayAccess implements Expression, Optimizable, Assignable {

    private final Expression receiver;
    private final Expression index;

    public ArrayAccess(Expression receiver, Expression index) {
        this.receiver = receiver;
        this.index = index;
    }

    @Override
    public Result assign(Result value, ExecutionContext context) {
        var result = receiver.evaluate(context);
        var idx = index.evaluate(context).intValue();
        result.set(idx, value);
        return value;
    }

    @Override
    public Result assign(Function<Result, Result> operator, ExecutionContext context) {
        var result = receiver.evaluate(context);
        var idx = index.evaluate(context).intValue();
        var value = operator.apply(result.get(idx));
        result.set(idx, value);
        return value;
    }

    @Override
    public Result evaluate(final ExecutionContext context) {
        var result = receiver.evaluate(context);
        var idx = index.evaluate(context).intValue();
        return result.get(idx);
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitArrayAccess(this);
    }

    @Override
    public boolean isMutable() {
        return receiver.isMutable() || index.isMutable();
    }

    @Override
    public String toString() {
        return receiver + "[" + index + "]";
    }

    public Expression receiver() {
        return receiver;
    }

    public Expression index() {
        return index;
    }
}
