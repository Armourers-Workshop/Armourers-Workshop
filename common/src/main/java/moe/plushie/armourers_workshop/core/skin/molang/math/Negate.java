package moe.plushie.armourers_workshop.core.skin.molang.math;

/**
 * Negate operator class This class is responsible for negating given value
 */
public class Negate implements IMathValue {

    public IMathValue value;

    public Negate(IMathValue value) {
        this.value = value;
    }

    @Override
    public double get() {
        return value.get() == 0 ? 1 : 0;
    }

    @Override
    public String toString() {
        return "!" + value.toString();
    }

    @Override
    public boolean isConstant() {
        return value.isConstant();
    }
}
