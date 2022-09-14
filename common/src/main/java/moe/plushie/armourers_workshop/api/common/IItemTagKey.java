package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IItemTagKey<T extends Item> extends IRegistryKey<Predicate<ItemStack>> {

    default boolean contains(ItemStack val) {
        return get().test(val);
    }
}
