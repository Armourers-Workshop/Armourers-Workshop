package moe.plushie.armourers_workshop.core.skin.molang.core;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns <b>1</b> if the contained value is equal to <b>0</b>, otherwise returns <b>0</b>
 */
public final class BooleanNegate implements Expression {

    private final Expression value;

    public BooleanNegate(Expression value) {
        this.value = value;
    }

    @Override
    public boolean isMutable() {
        return value.isMutable();
    }

    @Override
    public double getAsDouble() {
        return value.getAsDouble() == 0 ? 1 : 0;
    }

    @Override
    public String toString() {
        return "!" + value;
    }
}
