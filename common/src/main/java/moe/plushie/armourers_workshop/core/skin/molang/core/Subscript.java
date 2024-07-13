package moe.plushie.armourers_workshop.core.skin.molang.core;

public final class Subscript implements Expression {

    private final Expression variable;
    private final Expression index;

    public Subscript(Expression variable, Expression index) {
        this.variable = variable;
        this.index = index;
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
}
