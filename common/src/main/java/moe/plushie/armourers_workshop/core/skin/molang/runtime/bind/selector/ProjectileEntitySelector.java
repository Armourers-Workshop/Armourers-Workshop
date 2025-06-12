package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

public interface ProjectileEntitySelector {

    boolean isOnGround();

    double onGroundTime();

    boolean isSpectral();

    Object owner();

    double distanceFromMove();
}
