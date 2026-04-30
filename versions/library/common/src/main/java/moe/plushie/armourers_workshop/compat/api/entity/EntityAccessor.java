package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@Available("[16, )")
public interface EntityAccessor {

    default Entity aw2$self() {
        return (Entity) this;
    }

    default int aw2$id() {
        return aw2$self().getId();
    }

    default EntityType<?> aw2$type() {
        return aw2$self().getType();
    }

    default double aw2$x() {
        return aw2$self().getX();
    }

    default double aw2$y() {
        return aw2$self().getY();
    }

    default double aw2$z() {
        return aw2$self().getZ();
    }

    default double aw2$xOld() {
        return aw2$self().xOld;
    }

    default double aw2$yOld() {
        return aw2$self().yOld;

    }

    default double aw2$zOld() {
        return aw2$self().zOld;
    }

    default boolean aw2$isInvisible() {
        return aw2$self().isInvisible();
    }
}
