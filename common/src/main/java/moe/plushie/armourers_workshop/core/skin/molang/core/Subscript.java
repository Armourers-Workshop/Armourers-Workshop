package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

/**
 * Array accessing expression implementation, access to a value in
 * an array, by its index.
 *
 * <p>Example array accessing expressions: {@code my_geometries[0]},
 * {@code array.my_geometries[query.anim_time]}, {@code array.my_geos[math.cos(90)]}</p>
 */
public final class Subscript implements Expression {

    private final Variable variable;
    private final Expression index;

    public Subscript(Variable variable, Expression index) {
        this.variable = variable;
        this.index = index;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitSubscript(this);
    }

    @Override
    public boolean isMutable() {
        return variable.isMutable();
    }

    @Override
    public double getAsDouble() {
        // TODO: NO IMP
        return 0;
    }

    @Override
    public String toString() {
        return variable + "[" + index + "]";
    }

    public Variable variable() {
        return variable;
    }

    public Expression index() {
        return index;
    }
}
