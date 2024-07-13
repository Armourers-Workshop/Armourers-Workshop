package moe.plushie.armourers_workshop.core.skin.molang.core;


import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

/**
 * Identifier expression implementation for Molang.
 *
 * <p>Note that, identifiers in Molang are always
 * <b>case-insensitive</b></p>
 *
 * <p>Example identifier expressions: {@code math},
 * {@code name}, {@code this}, {@code print}</p>
 */
public final class Identifier implements Expression, StringSupplier {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitIdentifier(this);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public double getAsDouble() {
        return 0;
    }

    @Override
    public String getAsString() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
