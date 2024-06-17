package moe.plushie.armourers_workshop.core.skin.molang.math;

/**
 * Ternary operator class This value implementation allows to return different values depending on given condition value
 */
public class Ternary implements IValue {

    public final IValue condition;
    public final IValue ifTrue;
    public final IValue ifFalse;

    public Ternary(IValue condition, IValue ifTrue, IValue ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    @Override
    public double get() {
        return condition.get() != 0 ? ifTrue.get() : ifFalse.get();
    }

    @Override
    public String toString() {
        return condition.toString() + " ? " + ifTrue.toString() + " : " + ifFalse.toString();
    }

    @Override
    public boolean isConstant() {
        return condition.isConstant() && ifTrue.isConstant() && ifFalse.isConstant();
    }
}
