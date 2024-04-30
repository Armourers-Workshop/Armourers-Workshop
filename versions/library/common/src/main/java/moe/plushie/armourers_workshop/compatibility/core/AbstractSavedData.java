package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

@Available("[1.21, )")
public abstract class AbstractSavedData extends SavedData {

    public abstract void readAdditionalData(IDataSerializer serializer);

    public abstract void writeAdditionalData(IDataSerializer serializer);

    @Override
    public final CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        writeAdditionalData(AbstractDataSerializer.wrap(tag, provider));
        return tag;
    }
}
