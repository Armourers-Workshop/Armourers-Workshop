package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

@Available("[16, )")
public interface LivingEntityAccessor extends EntityAccessor {

    @Override
    default LivingEntity aw2$self() {
        return (LivingEntity) this;
    }

    default float aw2$scale() {
        return aw2$self().getScale();
    }

    default float aw2$hurtTime() {
        return aw2$self().hurtTime;
    }

    default float aw2$deathTime() {
        return aw2$self().deathTime;
    }

    default ItemStack aw2$getUseItem() {
        return aw2$self().getUseItem();
    }

    default int aw2$getTicksUsingItem() {
        return aw2$self().getTicksUsingItem();
    }

    default AABB aw2$getBoundingBox() {
        return aw2$self().getBoundingBox();
    }

    default boolean aw2$isFlying() {
        return aw2$isFallFlying();
    }

    default boolean aw2$isFallFlying() {
        return aw2$self().isFallFlying();
    }

    default boolean aw2$isBaby() {
        return aw2$self().isBaby();
    }
}
