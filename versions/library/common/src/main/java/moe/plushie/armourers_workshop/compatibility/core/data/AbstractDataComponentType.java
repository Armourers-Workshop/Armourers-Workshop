package moe.plushie.armourers_workshop.compatibility.core.data;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IDataComponentType;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

@Available("[1.21, )")
public class AbstractDataComponentType<T> implements DataComponentType<T>, IDataComponentType<T> {

    protected final DataComponentType<T> key;
    protected final String tag;
    protected final Codec<T> codec;
    protected final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

    public AbstractDataComponentType(String tag, Codec<T> codec) {
        this.tag = tag;
        this.codec = codec;
        this.streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec);
        this.key = this;
    }

    public static <T> AbstractDataComponentType<T> create(String tag, Codec<T> codec) {
        // forward to vanilla.
        if (tag.equals("EntityTag")) {
            return new Proxy<>(DataComponents.ENTITY_DATA, tag, codec);
        }
        // forward to vanilla.
        if (tag.equals("BlockEntityTag")) {
            return new Proxy<>(DataComponents.BLOCK_ENTITY_DATA, tag, codec);
        }
        return new AbstractDataComponentType<>(tag, codec);
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
        return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    public static class Proxy<T> extends AbstractDataComponentType<T> {

        protected final DataComponentType<CustomData> target;

        public Proxy(DataComponentType<CustomData> target, String tag, Codec<T> codec) {
            super(tag, codec);
            this.target = target;
        }

        @Override
        public boolean has(ItemStack itemStack) {
            return itemStack.has(target);
        }

        @Override
        public void set(ItemStack itemStack, @Nullable T value) {
            if (value != null) {
                itemStack.set(target, CustomData.of(encode(value)));
            } else {
                itemStack.set(target, null);
            }
        }

        @Nullable
        @Override
        public T get(ItemStack itemStack) {
            CustomData customData = itemStack.get(target);
            if (customData != null) {
                return decode(customData.getUnsafe());
            }
            return null;
        }

        @Override
        public T getOrDefault(ItemStack itemStack, T defaultValue) {
            CustomData customData = itemStack.get(target);
            if (customData != null) {
                return decode(customData.getUnsafe());
            }
            return defaultValue;
        }

        @Override
        public void remove(ItemStack itemStack) {
            itemStack.remove(target);
        }

        private CompoundTag encode(T value) {
            return ObjectUtils.unsafeCast(value);
        }

        private T decode(CompoundTag tag) {
            return ObjectUtils.unsafeCast(tag);
        }
    }
}
