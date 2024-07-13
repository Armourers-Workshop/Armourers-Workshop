package moe.plushie.armourers_workshop.core.skin.molang.core;

public final class Identifier implements Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
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
        return name;
    }

}
