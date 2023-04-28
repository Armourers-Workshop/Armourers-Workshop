package moe.plushie.armourers_workshop.api.common;

import net.minecraft.nbt.CompoundTag;

public interface ISavedData {

    void load(CompoundTag tag);

    void save(CompoundTag tag);

    void setDirty();
}
