package moe.plushie.armourers_workshop.core.skin.molang.core;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Negated equivalent of the stored value; returning a positive number if the stored value is negative, or a negative value if the stored value is positive
 */
public final class Negative implements Expression {

    private final Expression value;

    public Negative(Expression value) {
        this.value = value;
    }

    @Override
    public boolean isMutable() {
        return value.isMutable();
    }

    @Override
    public double getAsDouble() {
        return -value.getAsDouble();
    }

    @Override
    public String toString() {
        if (value instanceof Constant) {
            return "-" + value;
        }
        return "-" + "(" + value + ")";
    }
}
