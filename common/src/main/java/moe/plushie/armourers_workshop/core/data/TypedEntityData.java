package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.compat.core.data.AbstractTypedEntityData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import net.minecraft.nbt.CompoundTag;

@SuppressWarnings("unused")
public abstract class TypedEntityData<T> {

    public static <T> IDataCodec<TypedEntityData<T>> codec(TypedRegistry<T> registry) {
        var codec = AbstractTypedEntityData.createRegistryCodec(registry);
        return codec.xmap(Objects::unsafeCast, Objects::unsafeCast);
    }

    public static <T extends IEntityType<?>> TypedEntityData<T> of(T type, CompoundTag tag) {
        if (!tag.isEmpty()) {
            return new AbstractTypedEntityData<>(type.get(), tag);
        }
        return null;
    }

    public static <T extends IBlockEntityType<?>> TypedEntityData<T> of(T type, CompoundTag tag) {
        if (!tag.isEmpty()) {
            return new AbstractTypedEntityData<>(type.get(), tag);
        }
        return null;
    }

    public boolean contains(String string) {
        return tag().contains(string);
    }

    public boolean isEmpty() {
        return tag().isEmpty();
    }

    public abstract CompoundTag tag();
}
