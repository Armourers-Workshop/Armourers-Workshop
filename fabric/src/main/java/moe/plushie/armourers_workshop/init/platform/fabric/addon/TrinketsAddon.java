package moe.plushie.armourers_workshop.init.platform.fabric.addon;

import moe.plushie.armourers_workshop.core.data.EntityEquipmentManager;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class TrinketsAddon {

    public static <T> void register(BiFunction<LivingEntity, Predicate<ItemStack>, Collection<T>> provider, Function<T, ItemStack> transform) {
        EntityEquipmentManager.register(new EntityEquipmentManager.Provider() {
            @Override
            public Iterable<ItemStack> getArmorSlots(Entity entity) {
                if (entity instanceof LivingEntity) {
                    Collection<T> collection = provider.apply((LivingEntity) entity, Objects::nonNull);
                    if (collection != null) {
                        return Collections.compactMap(collection, transform);
                    }
                }
                return null;
            }
        });
    }
}
