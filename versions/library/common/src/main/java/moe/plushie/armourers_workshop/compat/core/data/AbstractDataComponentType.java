package moe.plushie.armourers_workshop.compat.core.data;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Available("[1.21, )")
public class AbstractDataComponentType<T> implements DataComponentType<T>, IDataComponentType<T> {

    protected final DataComponentType<T> key;
    protected final String tag;
    protected final IDataCodec<T> codec;
    protected final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

    public AbstractDataComponentType(String tag, IDataCodec<T> codec) {
        this.tag = tag;
        this.codec = codec;
        this.streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        this.key = this;
    }

    public static <T> DataComponentType<T> unwrap(IDataComponentType<T> type) {
        return (AbstractDataComponentType<T>) type;
    }

    public static <T> AbstractDataComponentType<T> create(String tag, IDataCodec<T> codec) {
        // forward to vanilla.
        if (tag.equals("EntityTag")) {
            return new Proxy<>(tag, codec, () -> DataComponents.ENTITY_DATA);
        }
        // forward to vanilla.
        if (tag.equals("BlockEntityTag")) {
            return new Proxy<>(tag, codec, () -> DataComponents.BLOCK_ENTITY_DATA);
        }
        return new AbstractDataComponentType<>(tag, codec);
    }

    public boolean isProxy() {
        return this instanceof Proxy;
    }

    @Override
    public boolean has(ItemStack itemStack) {
        return itemStack.has(key);
    }

    @Override
    public void set(ItemStack itemStack, @Nullable T value) {
        itemStack.set(key, value);
    }

    @Nullable
    @Override
    public T get(ItemStack itemStack) {
        return itemStack.get(key);
    }

    @Override
    public T getOrDefault(ItemStack itemStack, T defaultValue) {
        return itemStack.getOrDefault(key, defaultValue);
    }

    @Override
    public void remove(ItemStack itemStack) {
        itemStack.remove(key);
    }

    @Nullable
    @Override
    public Codec<T> codec() {
        return codec.codec();
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    public static class Proxy<S, D> extends AbstractDataComponentType<S> {

        protected final AbstractTypedEntityData.Serializer<S, D> serializer;

        public Proxy(String tag, IDataCodec<S> codec, AbstractTypedEntityData.Serializer<S, D> serializer) {
            super(tag, codec);
            this.serializer = serializer;
        }

        @Override
        public boolean has(ItemStack itemStack) {
            return itemStack.has(serializer.key());
        }

        @Override
        public void set(ItemStack itemStack, @Nullable S value) {
            if (value != null) {
                itemStack.set(serializer.key(), serializer.encode(value));
            } else {
                itemStack.set(serializer.key(), null);
            }
        }

        @Nullable
        @Override
        public S get(ItemStack itemStack) {
            var entityData = itemStack.get(serializer.key());
            if (entityData != null) {
                return serializer.decode(entityData);
            }
            return null;
        }

        @Override
        public S getOrDefault(ItemStack itemStack, S defaultValue) {
            var entityData = itemStack.get(serializer.key());
            if (entityData != null) {
                return serializer.decode(entityData);
            }
            return defaultValue;
        }

        @Override
        public void remove(ItemStack itemStack) {
            itemStack.remove(serializer.key());
        }
    }
}
