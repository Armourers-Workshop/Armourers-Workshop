package moe.plushie.armourers_workshop.api.action;

public interface ICanRotation {

    default boolean isMirror() {
        return false;
    }
}
