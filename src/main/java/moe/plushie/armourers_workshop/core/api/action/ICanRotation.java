package moe.plushie.armourers_workshop.core.api.action;

public interface ICanRotation {

    default boolean isMirror() {
        return false;
    }
}
