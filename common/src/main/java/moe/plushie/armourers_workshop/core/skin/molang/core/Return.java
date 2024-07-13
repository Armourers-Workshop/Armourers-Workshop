package moe.plushie.armourers_workshop.core.skin.molang.core;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Interrupt flow and return a stored value.
 */
public final class Return implements Expression {

    private final Expression value;

    public Return(Expression value) {
        this.value = value;
    }

    @Override
    public boolean isMutable() {
        return value.isMutable();
    }

    @Override
    public double getAsDouble() {
        return value.getAsDouble();
    }

    @Override
    public String toString() {
        return "return " + value;
    }
}
