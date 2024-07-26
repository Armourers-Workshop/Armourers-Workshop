package moe.plushie.armourers_workshop.core.skin.molang.impl;

import moe.plushie.armourers_workshop.core.skin.molang.core.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public interface Supplier extends IntSupplier, DoubleSupplier {

    @Override
    double getAsDouble();

    @Override
    default int getAsInt() {
        return MathHelper.floor(getAsDouble());
    }

    default float getAsFloat() {
        return (float) getAsDouble();
    }

    default boolean getAsBoolean() {
        return getAsDouble() != 0.0;
    }

    default String getAsString() {
        return String.valueOf(getAsDouble());
    }

    default Expression getAsExpression() {
        return new Constant(getAsDouble());
    }
}
