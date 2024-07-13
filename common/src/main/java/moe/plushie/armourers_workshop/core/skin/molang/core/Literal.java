package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

public final class Literal implements Expression, StringSupplier {

    private final String value;

    public Literal(String value) {
        this.value = value;
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitLiteral(this);
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
        return value;
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
