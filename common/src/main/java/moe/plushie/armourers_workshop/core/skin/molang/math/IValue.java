package moe.plushie.armourers_workshop.core.skin.molang.math;

/**
 * Math value interface This interface provides only one method which is used by all mathematical related classes. The
 * point of this interface is to provide generalized abstract method for computing/fetching some value from different
 * mathematical classes.
 */
public interface IValue {

    /**
     * Get computed or stored value
     */
    double get();

    /**
     * Whether is a constant value.
     */
    boolean isConstant();
}
