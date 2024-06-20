package moe.plushie.armourers_workshop.api.data;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface IDataSerializer {

    <T> T read(IDataSerializerKey<T> key);

    <T> void write(IDataSerializerKey<T> key, T value);

    void readItemList(NonNullList<ItemStack> items);

    void writeItemList(NonNullList<ItemStack> items);
}
