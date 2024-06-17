package moe.plushie.armourers_workshop.core.skin.molang.math;

import moe.plushie.armourers_workshop.init.ModLog;

public class Literal extends Variable {

    public Literal(String name, double value) {
        super(name, value);
    }

    @Override
    public void set(double value) {
        // can't set value
        ModLog.warn("Attempted to set a literal value of " + getName());
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
