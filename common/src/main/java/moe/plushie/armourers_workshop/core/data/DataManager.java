package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class DataManager {

    public static IDataSerializer createEntityDataReader(Entity entity, CompoundTag inputTag) {
        return AbstractDataSerializer.wrap(inputTag, entity);
    }

    public static IDataSerializer createEntityDataWriter(Entity entity, CompoundTag outputTag) {
        return AbstractDataSerializer.wrap(outputTag, entity);
    }
}


