package moe.plushie.armourers_workshop.api.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface IConfigurableToolProperty<T> {

    String OPTIONS_KEY = "Options";

    String getName();

    T empty();

    T get(CompoundTag nbt);

    void set(CompoundTag nbt, T value);

    default T get(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        if (nbt != null && nbt.contains(OPTIONS_KEY, 10)) {
            return get(nbt.getCompound(OPTIONS_KEY));
        }
        return empty();
    }

    default void set(ItemStack itemStack, T value) {
        CompoundTag newNBT = null;
        CompoundTag nbt = itemStack.getTag();
        if (nbt != null) {
            newNBT = nbt.getCompound(OPTIONS_KEY);
        }
        if (newNBT == null) {
            newNBT = new CompoundTag();
        }
        set(newNBT, value);
        if (newNBT.size() != 0) {
            itemStack.getOrCreateTag().put(OPTIONS_KEY, newNBT);
        } else if (nbt != null) {
            nbt.remove(OPTIONS_KEY);
        }
    }
}
