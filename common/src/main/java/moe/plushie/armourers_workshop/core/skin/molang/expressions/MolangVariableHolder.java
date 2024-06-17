package moe.plushie.armourers_workshop.core.skin.molang.expressions;

import moe.plushie.armourers_workshop.core.skin.molang.math.IValue;
import moe.plushie.armourers_workshop.core.skin.molang.math.Variable;

/**
 * Extension of {@link MolangValue} that additionally sets the value of a provided {@link Variable} when being called.
 */
public class MolangVariableHolder extends MolangValue {

    public Variable variable;

    public MolangVariableHolder(Variable variable, IValue value) {
        super(value);
        this.variable = variable;
    }

    @Override
    public double get() {
        double value = super.get();
        this.variable.set(value);
        return value;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString() {
        return this.variable.getName() + " = " + super.toString();
    }
}
