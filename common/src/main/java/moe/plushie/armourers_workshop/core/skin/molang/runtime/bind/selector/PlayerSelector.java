package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

public interface PlayerSelector {


    double elytraYaw();

    double elytraPitch();

    double elytraRoll();

    boolean hasCape();

    double capeFlapAmount();

    int foodLevel();

    double experience();

    boolean hasLeftShoulderParrot();

    boolean hasRightShoulderParrot();

    int leftShoulderParrotVariant();

    int rightShoulderParrotVariant();
}
