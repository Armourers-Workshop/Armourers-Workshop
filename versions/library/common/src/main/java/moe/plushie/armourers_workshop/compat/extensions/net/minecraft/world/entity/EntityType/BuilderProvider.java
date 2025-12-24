package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.EntityType;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.26)")
@Extension
public class BuilderProvider {

    @Extension
    public static class Builder {

        public static <T extends Entity> EntityType<T> build(@This EntityType.Builder<T> builder, OpenResourceLocation id) {
            return builder.build(id.path());
        }
    }
}
