package moe.plushie.armourers_workshop.compat.core.data;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.data.TypedEntityData;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.NotImplementedException;

@Available("[1.21, 1.22)")
public class AbstractTypedEntityData<T> extends TypedEntityData<T> {

    protected final CustomData impl;

    public AbstractTypedEntityData(Object type, CompoundTag tag) {
        var id = findKey(type);
        tag.putString("id", id.toString());
        this.impl = CustomData.of(tag);
    }

    public AbstractTypedEntityData(CustomData impl) {
        this.impl = impl;
    }

    public static <T> IDataCodec<AbstractTypedEntityData<?>> createRegistryCodec(TypedRegistry<T> registry) {
        return IDataCodec.wrap(CustomData.CODEC).xmap(AbstractTypedEntityData::new, AbstractTypedEntityData::get);
    }

    @Override
    public CompoundTag tag() {
        return impl.getUnsafe();
    }

    public CustomData get() {
        return impl;
    }

    private static <T> ResourceLocation findKey(T type) {
        if (type instanceof EntityType<?> type1) {
            return BuiltInRegistries.ENTITY_TYPE.getKey(type1);
        }
        if (type instanceof BlockEntityType<?> type1) {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type1);
        }
        throw new NotImplementedException("not supported entity type!!!");
    }

    @SuppressWarnings("unchecked")
    public interface Serializer<S, D> {

        DataComponentType<D> key();

        default D encode(S value) {
            if (value instanceof AbstractTypedEntityData<?> data) {
                return (D) data.get();
            }
            return null;
        }

        default S decode(D value) {
            if (value instanceof CustomData impl) {
                return (S) new AbstractTypedEntityData<>(impl);
            }
            return null;
        }
    }
}
