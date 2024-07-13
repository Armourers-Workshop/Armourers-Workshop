package moe.plushie.armourers_workshop.core.skin.molang.core;

public final class Literal implements Expression {

    private final String value;

    public Literal(String value) {
        this.value = value;
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
    public String toString() {
        return "'" + value + "'";
    }
}
