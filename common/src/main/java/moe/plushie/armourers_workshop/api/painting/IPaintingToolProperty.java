package moe.plushie.armourers_workshop.api.painting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface IPaintingToolProperty<T> {

    String getName();

    T empty();

    T get(CompoundTag nbt);

    void set(CompoundTag nbt, T value);

    default T get(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        if (nbt != null && nbt.contains("Options", 10)) {
            return get(nbt.getCompound("Options"));
        }
        return empty();
    }

    default void setValue(ItemStack itemStack, T value) {
        CompoundTag newNBT = null;
        CompoundTag nbt = itemStack.getTag();
        if (nbt != null) {
            newNBT = nbt.getCompound("Options");
        }
        if (newNBT == null) {
            newNBT = new CompoundTag();
        }
        set(newNBT, value);
        if (newNBT.size() != 0) {
            itemStack.getOrCreateTag().put("Options", newNBT);
        } else if (nbt != null) {
            nbt.remove("Options");
        }
    }
}
