package moe.plushie.armourers_workshop.compatibility.core.data;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.api.data.IDataSerializerKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.function.Supplier;

import manifold.ext.rt.api.auto;

@Available("[1.21, )")
public class AbstractDataSerializer implements IDataSerializer {

    private final CompoundTag tag;
    private final HolderLookup.Provider provider;

    public AbstractDataSerializer(CompoundTag tag, HolderLookup.Provider provider) {
        this.tag = tag;
        this.provider = provider;
    }

    public static AbstractDataSerializer wrap(CompoundTag tag, Entity entity) {
        return wrap(tag, entity.registryAccess());
    }

    public static AbstractDataSerializer wrap(CompoundTag tag, BlockEntity blockEntity) {
        return wrap(tag, blockEntity.getLevel().registryAccess());
    }

    public static AbstractDataSerializer wrap(CompoundTag tag, Level level) {
        return wrap(tag, level.registryAccess());
    }

    public static AbstractDataSerializer wrap(CompoundTag tag, HolderLookup.Provider provider) {
        return new AbstractDataSerializer(tag, provider);
    }


    @Override
    public <T> T read(IDataSerializerKey<T> key) {
        String name = key.getName();
        if (tag.contains(name)) {
            Codec<T> codec = key.getCodec();
            auto value = codec.decode(NbtOps.INSTANCE, tag.get(key.getName())).result();
            if (value.isPresent()) {
                T value2 = value.get().getFirst();
                if (value2 != null) {
                    return value2;
                }
            }
        }
        Supplier<T> constructor = key.getConstructor();
        if (constructor != null) {
            return constructor.get();
        }
        return key.getDefault();
    }

    @Override
    public <T> void write(IDataSerializerKey<T> key, T value) {
        T defaultValue = key.getDefault();
        if (defaultValue == value || Objects.equals(defaultValue, value)) {
            return;
        }
        String name = key.getName();
        Codec<T> codec = key.getCodec();
        codec.encodeStart(NbtOps.INSTANCE, value).result().ifPresent(it -> {
            // we need to merge new value into the item.
            tag.put(name, it);
        });
    }

    @Override
    public void readItemList(NonNullList<ItemStack> items) {
        ContainerHelper.loadAllItems(tag, items, provider);
    }

    @Override
    public void writeItemList(NonNullList<ItemStack> items) {
        ContainerHelper.saveAllItems(tag, items, provider);
    }

    @Override
    public CompoundTag getUnsafe() {
        return tag;
    }
}
