package moe.plushie.armourers_workshop.core.skin.molang.expressions;

import moe.plushie.armourers_workshop.core.skin.molang.MolangParser;
import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;

/**
 * Molang extension for the {@link IValue} system. Used to handle values and expressions specific to Molang
 * deserialization
 */
public class MolangValue implements IValue {

    private final IValue value;

    private final boolean returns;

    public MolangValue(IValue value) {
        this(value, false);
    }

    public MolangValue(IValue value, boolean isReturn) {
        this.value = value;
        this.returns = isReturn;
    }

    @Override
    public double get() {
        return this.value.get();
    }

    public IValue getValueHolder() {
        return this.value;
    }

    public boolean isReturnValue() {
        return this.returns;
    }

    @Override
    public boolean isConstant() {
        return value.isConstant();
    }

    @Override
    public String toString() {
        return (this.returns ? MolangParser.RETURN : "") + this.value.toString();
    }
}
