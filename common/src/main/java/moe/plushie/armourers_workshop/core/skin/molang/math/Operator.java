package moe.plushie.armourers_workshop.core.skin.molang.math;

/**
 * Operator class This class is responsible for performing a calculation of two values based on given operation.
 */
public class Operator implements IMathValue {

    public Operation operation;

    public IMathValue a;
    public IMathValue b;

    public Operator(Operation op, IMathValue a, IMathValue b) {
        this.operation = op;
        this.a = a;
        this.b = b;
    }

    @Override
    public double get() {
        return operation.calculate(a.get(), b.get());
    }

    @Override
    public String toString() {
        return a.toString() + " " + operation.sign + " " + b.toString();
    }

    @Override
    public boolean isConstant() {
        return a.isConstant() && b.isConstant();
    }
}
