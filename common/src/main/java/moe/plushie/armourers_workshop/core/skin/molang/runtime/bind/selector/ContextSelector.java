package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

public interface ContextSelector {

    float partialTick();

    double animationTicks();

    double animTime();

    double lifeTime();

    double fps();

    int entityCount();

    LevelSelector level();

    boolean isFirstPerson();

    boolean isRenderingInInventory();

    boolean isRenderingInFirstPersonMod();
}
