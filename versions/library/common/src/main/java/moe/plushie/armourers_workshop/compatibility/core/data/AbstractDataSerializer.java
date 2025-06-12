package moe.plushie.armourers_workshop.compatibility.core.data;

import com.mojang.serialization.DynamicOps;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@Available("[1.21, )")
public class AbstractDataSerializer implements IDataSerializer {

    protected final CompoundTag tag;
    protected final HolderLookup.Provider provider;
    protected final DynamicOps<Tag> ops;

    public AbstractDataSerializer(CompoundTag tag, @Nullable Object context) {
        this.tag = tag;
        this.provider = unwrap(context);
        if (provider != null) {
            this.ops = provider.createSerializationContext(NbtOps.INSTANCE);
        } else {
            this.ops = NbtOps.INSTANCE;
        }
    }

    public static AbstractDataSerializer wrap(CompoundTag tag, @Nullable Object context) {
        return new AbstractDataSerializer(tag, context);
    }

    private static HolderLookup.Provider unwrap(@Nullable Object context) {
        if (context instanceof HolderLookup.Provider provider) {
            return provider;
        }
        if (context instanceof Level level) {
            return level.registryAccess();
        }
        if (context instanceof Entity entity) {
            return entity.registryAccess();
        }
        if (context instanceof BlockEntity blockEntity) {
            return unwrap(blockEntity.getLevel());
        }
        return null;
    }

    @Override
    public <T> T read(IDataSerializerKey<T> key) {
        var name = key.name();
        if (tag != null && tag.contains(name)) {
            var codec = key.codec().codec();
            var value = codec.decode(ops, tag.get(key.name())).result();
            if (value.isPresent()) {
                T value2 = value.get().getFirst();
                if (value2 != null) {
                    return value2;
                }
            }
        }
        var constructor = key.constructor();
        if (constructor != null) {
            return constructor.get();
        }
        return key.defaultValue();
    }

    @Override
    public <T> void write(IDataSerializerKey<T> key, T value) {
        if (tag == null) {
            return;
        }
        var defaultValue = key.defaultValue();
        if (defaultValue == value || Objects.equals(defaultValue, value)) {
            return;
        }
        var name = key.name();
        var codec = key.codec().codec();
        codec.encodeStart(ops, value).result().ifPresent(it -> {
            // we need to merge new value into the item.
            tag.put(name, it);
        });
    }
}
