package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

@Available("[1.18, )")
public abstract class AbstractSavedData extends SavedData {

    public abstract void load(CompoundTag tag);

    @Override
    public abstract CompoundTag save(CompoundTag tag);
}
