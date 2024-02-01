package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Supplier;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class PlaceholderManager {

    private static final int PLACEHOLDER_ENTITY_ID = -1021;

    public static final Supplier<MannequinEntity> MANNEQUIN = new LazyEntry<>(level -> {
        auto entity = new MannequinEntity(ModEntityTypes.MANNEQUIN.get().get(), level);
        entity.setExtraRenderer(false); // never magic cir
        return entity;
    });


    public static boolean isPlaceholder(Entity entity) {
        return entity.getId() == PLACEHOLDER_ENTITY_ID;
    }

    private static class LazyEntry<T extends Entity> implements Supplier<T> {

        private T entity;
        private final Function<Level, T> provider;

        private LazyEntry(Function<Level, T> provider) {
            this.provider = provider;
        }

        @Override
        public T get() {
            auto level = Minecraft.getInstance().level;
            if (entity == null) {
                entity = provider.apply(level);
                entity.setId(PLACEHOLDER_ENTITY_ID);
            }
            if (entity.getLevel() != level) {
                entity.setLevel(level);
            }
            return entity;
        }
    }
}
