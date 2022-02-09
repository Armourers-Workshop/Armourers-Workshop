package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.network.PacketBuffer;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketBufferCoder<T> {

    public Class<T> type;
    public Function<PacketBuffer, T> decoder;
    public BiConsumer<T, PacketBuffer> encoder;

    public PacketBufferCoder(Class<T> type, Function<PacketBuffer, T> decoder, BiConsumer<T, PacketBuffer> encoder) {
        this.type = type;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacketBufferCoder<?> that = (PacketBufferCoder<?>) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
