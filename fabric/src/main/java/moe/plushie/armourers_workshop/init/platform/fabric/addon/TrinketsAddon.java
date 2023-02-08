package moe.plushie.armourers_workshop.init.platform.fabric.addon;

import dev.emi.trinkets.api.TrinketComponent;
import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import moe.plushie.armourers_workshop.core.data.ItemStackProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrinketsAddon {

    public static <T> void register(BiFunction<LivingEntity, Predicate<ItemStack>, Collection<T>> provider, Function<T, ItemStack> transform) {
        ItemStackProvider.getInstance().register(new IItemStackProvider() {
            @Override
            public Iterable<ItemStack> getArmorSlots(Entity entity) {
                if (entity instanceof LivingEntity) {
                    Collection<T> collection = provider.apply((LivingEntity) entity, Objects::nonNull);
                    if (collection != null) {
                        return collection.stream().map(transform).collect(Collectors.toList());
                    }
                }
                return null;
            }

            @Override
            public Iterable<ItemStack> getHandSlots(Entity entity) {
                return null;
            }
        });
    }
}
