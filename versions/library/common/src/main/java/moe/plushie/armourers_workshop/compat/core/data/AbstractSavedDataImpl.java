package moe.plushie.armourers_workshop.compat.core.data;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

@Available("[1.21, 1.22)")
public abstract class AbstractSavedDataImpl extends SavedData implements IDataSerializable.Mutable {

    @Override
    public final CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        serialize(new TagSerializer(tag, SerializationContext.from(provider)));
        return tag;
    }
}
