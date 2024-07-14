package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.Property;
import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

/**
 * Array accessing expression implementation, access to a value in
 * an array, by its index.
 *
 * <p>Example array accessing expressions: {@code my_geometries[0]},
 * {@code array.my_geometries[query.anim_time]}, {@code array.my_geos[math.cos(90)]}</p>
 */
public final class Subscript implements Expression, Property {

    private final Expression variable;
    private final Expression index;

    public Subscript(Expression variable, Expression index) {
        this.variable = variable;
        this.index = index;
    }

    @Override
    public void update(Expression expression) {
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitSubscript(this);
    }

    @Override
    public boolean isMutable() {
        return variable.isMutable() || index.isMutable();
    }

    @Override
    public double getAsDouble() {
        return getAsExpression().getAsDouble();
    }

    @Override
    public Expression getAsExpression() {
        return Constant.ZERO;
    }

    @Override
    public String toString() {
        return variable + "[" + index + "]";
    }

    public Expression variable() {
        return variable;
    }

    public Expression index() {
        return index;
    }
}
