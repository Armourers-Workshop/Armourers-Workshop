package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

public interface PlayerSelector {

    float partialTick();

    default double elytraYaw() {
        return getElytraYaw(partialTick());
    }

    default double elytraPitch() {
        return getElytraPitch(partialTick());
    }

    default double elytraRoll() {
        return getElytraRoll(partialTick());
    }

    double getElytraYaw(float partialTick);

    double getElytraPitch(float partialTick);

    double getElytraRoll(float partialTick);

    boolean hasCape();

    double capeFlapAmount();

    int foodLevel();

    double experience();

    boolean hasLeftShoulderParrot();

    boolean hasRightShoulderParrot();

    int leftShoulderParrotVariant();

    int rightShoulderParrotVariant();
}
