package moe.plushie.armourers_workshop.core.skin.molang.core;

public final class Continue implements Expression {

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
        return "continue";
    }
}
