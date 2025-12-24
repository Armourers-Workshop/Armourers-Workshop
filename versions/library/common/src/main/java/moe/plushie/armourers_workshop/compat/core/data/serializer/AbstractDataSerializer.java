package moe.plushie.armourers_workshop.compat.core.data.serializer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@Available("[1.16, 1.26)")
public interface AbstractDataSerializer extends IDataSerializer {

    static IDataSerializer wrap(CompoundTag tag, @Nullable Object context) {
        return new AbstractTagDataSerializer(tag, SerializationContext.from(context));
    }

    static IDataSerializer wrap(CompoundTag tag) {
        return new AbstractTagDataSerializer(tag, SerializationContext.EMPTY);
    }

    static CompoundTag unwrap(IDataSerializer serializer) {
        if (serializer instanceof AbstractTagDataSerializer valueDataSerializer) {
            return valueDataSerializer.tag();
        }
        throw new RuntimeException("can't supported type " + serializer.getClass().getSimpleName());
    }
}
