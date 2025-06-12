package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface LivingEntitySelector {

    double bodyYaw();

    double bodyPitch();


    double health();

    double maxHealth();


    double armorValue();


    double hurtTime();


    boolean isDeadOrDying();

    boolean isEating();

    boolean isSleeping();

    boolean isUsingItem();


    boolean isAutoSpinAttack();

    boolean isOnClimbable();


    double usingItemDuration();

    double usingItemMaxDuration();

    double usingItemRemainingDuration();

    int arrowCount();

    int stingerCount();

    double attributeValueByName(String name);

    @Nullable
    EffectSelector effectByName(String name);

    @Nullable
    ItemSelector equippedItemBySlot(String slot);

    int equipmentCount();

    int lastClimbableFacing();
}
