package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PlaceholderManager {

    private static final int PLACEHOLDER_ENTITY_ID = -1021;

    public static final Supplier<MannequinEntity> MANNEQUIN = new LazyEntry<>(level -> {
        var entity = new MannequinEntity(ModEntityTypes.MANNEQUIN.get().get(), level);
        entity.setExtraRenderer(false); // never magic cir
        return entity;
    });


    public static boolean isPlaceholder(Entity entity) {
        if (entity != null) {
            return entity.getId() == PLACEHOLDER_ENTITY_ID;
        }
        return false;
    }

    private static class LazyEntry<T extends Entity> implements Supplier<T> {

        private T entity;
        private final Function<Level, T> provider;

        private LazyEntry(Function<Level, T> provider) {
            this.provider = provider;
        }

        @Override
        public T get() {
            var level = EnvironmentManager.getClient().level;
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
