package moe.plushie.armourers_workshop.core.skin.molang.math;

/**
 * Constant class This class simply returns supplied in the constructor value
 */
public class Constant implements IMathValue {

    private final double value;

    public Constant(double value) {
        this.value = value;
    }

    @Override
    public double get() {
        return value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
