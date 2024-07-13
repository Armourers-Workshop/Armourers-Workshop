package moe.plushie.armourers_workshop.core.skin.molang.core;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * An immutable double value
 */
public final class Constant implements Expression {

    public static final Constant ONE = new Constant(1.0);
    public static final Constant ZERO = new Constant(0.0);

    private final double value;

    public Constant(double value) {
        this.value = value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
