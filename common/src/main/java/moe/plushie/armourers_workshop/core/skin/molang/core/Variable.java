package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.impl.Property;
import moe.plushie.armourers_workshop.core.skin.molang.impl.Visitor;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * {@link Expression} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * Returns the currently stored value, which may be modified at any given time via {@link #set}. Values may be lazily evaluated to eliminate wasteful usage
 */
public final class Variable implements Expression, Property {

    private final String name;

    private int type = 0;
    private double value;
    private DoubleSupplier provider;

    public Variable(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public Variable(String name, DoubleSupplier provider) {
        this.name = name;
        this.provider = provider;
    }

    @Override
    public void update(Expression expression) {
        set(expression);
    }

    @Override
    public Expression visit(Visitor visitor) {
        return visitor.visitVariable(this);
    }

    public void set(final double value) {
        this.value = value;
        this.provider = null;
        this.type = 1;
    }

    public void set(final DoubleSupplier value) {
        this.provider = value;
        this.type = 2;
    }

    public void set(final boolean value) {
        set(value ? 1.0 : 0.0);
    }

    public void set(final BooleanSupplier value) {
        set(() -> value.getAsBoolean() ? 1.0 : 0.0);
    }

    @Override
    public boolean isNull() {
        return type == 0;
    }

    @Override
    public double getAsDouble() {
        if (provider != null) {
            return provider.getAsDouble();
        }
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }
}
