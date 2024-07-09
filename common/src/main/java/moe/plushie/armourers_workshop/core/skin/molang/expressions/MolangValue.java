package moe.plushie.armourers_workshop.core.skin.molang.expressions;

import moe.plushie.armourers_workshop.core.skin.molang.MolangParser;
import moe.plushie.armourers_workshop.core.skin.molang.math.IMathValue;

/**
 * Molang extension for the {@link IMathValue} system. Used to handle values and expressions specific to Molang
 * deserialization
 */
public class MolangValue implements IMathValue {

    private final IMathValue value;

    private final boolean returns;

    public MolangValue(IMathValue value) {
        this(value, false);
    }

    public MolangValue(IMathValue value, boolean isReturn) {
        this.value = value;
        this.returns = isReturn;
    }

    @Override
    public double get() {
        return this.value.get();
    }

    public IMathValue getValueHolder() {
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
