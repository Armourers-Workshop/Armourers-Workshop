package moe.plushie.armourers_workshop.api.common;

import net.minecraft.nbt.CompoundTag;

public interface IConfigurableToolProperty<T> {

    String getName();

    T empty();

    T get(CompoundTag nbt);

    void set(CompoundTag nbt, T value);
}
