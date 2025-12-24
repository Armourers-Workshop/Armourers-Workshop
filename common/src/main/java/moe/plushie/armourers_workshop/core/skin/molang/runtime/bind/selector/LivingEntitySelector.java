package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface LivingEntitySelector {

    float partialTick();

    default double bodyYaw() {
        return getBodyYaw(partialTick());
    }

    default double bodyPitch() {
        return getBodyPitch(partialTick());
    }

    double getBodyYaw(float partialTick);

    double getBodyPitch(float partialTick);

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

    int equipmentCount();

    int lastClimbableFacing();

    double attributeValueByName(String name);

    @Nullable
    EffectSelector effectByName(String name);

    @Nullable
    ItemSelector equipmentBySlot(String slot);
}
