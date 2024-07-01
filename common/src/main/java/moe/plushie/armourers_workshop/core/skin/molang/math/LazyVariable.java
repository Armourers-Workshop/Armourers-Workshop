package moe.plushie.armourers_workshop.core.skin.molang.math;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * Lazy override of Variable, to allow for deferred value calculation. <br>
 * Optimises rendering as values are not touched until needed (if at all)
 */
public class LazyVariable extends Variable {

    private DoubleSupplier valueSupplier;

    public LazyVariable(String name, double value) {
        super(name, value);
    }

    public LazyVariable(String name, DoubleSupplier valueSupplier) {
        super(name, 0);
        this.valueSupplier = valueSupplier;
    }

    /**
     * Instantiates a copy of this variable from this variable's current value and name
     */
    public static LazyVariable from(Variable variable) {
        return new LazyVariable(variable.getName(), variable.get());
    }

    /**
     * Set the new value for the variable, acting as a constant
     */
    @Override
    public void set(double value) {
        super.set(value);
        this.valueSupplier = null;
    }

    /**
     * Set the new value for the variable, acting as a constant
     */
    public void set(boolean value) {
        if (value) {
            set(1d);
        } else {
            set(0d);
        }
    }

    /**
     * Set the new value supplier for the variable
     */
    public void set(BooleanSupplier valueSupplier) {
        set(() -> {
            if (valueSupplier.getAsBoolean()) {
                return 1d;
            } else {
                return 0d;
            }
        });
    }

    /**
     * Set the new value supplier for the variable
     */
    public void set(DoubleSupplier valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    /**
     * Get the current value of the variable
     */
    @Override
    public double get() {
        if (valueSupplier != null) {
            set(valueSupplier.getAsDouble());
        }
        return super.get();
    }
}
