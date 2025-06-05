package moe.plushie.armourers_workshop.core.skin.serializer.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureAnimation;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureProperties;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.nbt.CompoundTag;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public interface IInputStream {

    static IInputStream of(InputStream stream) {
        if (stream instanceof DataInputStream dataInputStream) {
            return of(dataInputStream);
        }
        return of(new DataInputStream(stream));
    }

    static IInputStream of(DataInputStream stream) {
        return () -> stream;
    }

    DataInputStream inputStream();

    default void skipBytes(int len) throws IOException {
        inputStream().skipBytes(len);
    }

    default void read(byte[] b) throws IOException {
        inputStream().readFully(b);
    }

    default void read(byte[] b, int off, int len) throws IOException {
        inputStream().readFully(b, off, len);
    }

    default void read(FloatBuffer buffer) throws IOException {
        var stream = inputStream();
        int position = buffer.position();
        int limit = buffer.limit();
        for (int index = position; index < limit; ++index) {
            buffer.put(index, stream.readFloat());
        }
    }

    default ByteBuf readBytes(int limit) throws IOException {
        // we can't directly create a big buffers, it's easy to be hacked.
        var inputStream = inputStream();
        var buffers = new ArrayList<byte[]>();
        int remaining = limit;
        while (remaining > 0) {
            var bytes = new byte[Math.min(remaining, 16384)]; // 16k
            inputStream.readFully(bytes);
            buffers.add(bytes);
            remaining -= bytes.length;
        }
        return Unpooled.wrappedBuffer(buffers.toArray(new byte[0][]));
    }

    default byte readByte() throws IOException {
        return inputStream().readByte();
    }

    default boolean readBoolean() throws IOException {
        return inputStream().readBoolean();
    }

    default short readShort() throws IOException {
        return inputStream().readShort();
    }

    default int readInt() throws IOException {
        return inputStream().readInt();
    }

    default long readLong() throws IOException {
        return inputStream().readLong();
    }

    default float readFloat() throws IOException {
        return inputStream().readFloat();
    }

    default double readDouble() throws IOException {
        return inputStream().readDouble();
    }

    default int readFixedInt(int usedBytes) throws IOException {
        if (usedBytes == 4) {
            return readInt();
        }
        int value = 0;
        for (int i = 0; i < usedBytes; i++) {
            int ch = readByte() & 0xff;
            value = (value << 8) | ch;
        }
        return value;
    }

    default float readFixedFloat(int usedBytes) throws IOException {
        return Float.intBitsToFloat(readFixedInt(usedBytes));
    }

    default String readString() throws IOException {
        int size = inputStream().readUnsignedShort();
        return readString(size);
    }

    default String readString(int len) throws IOException {
        if (len <= 0) {
            return "";
        }
        byte[] bytes = new byte[len];
        inputStream().readFully(bytes, 0, len);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    default int readVarInt() throws IOException {
        var inputStream = inputStream();
        byte b;
        int i = 0;
        int j = 0;
        do {
            b = inputStream.readByte();
            i |= (b & 0x7F) << j++ * 7;
            if (j <= 5) {
                continue;
            }
            throw new RuntimeException("VarInt too big");
        } while ((b & 0x80) != 0);
        return i;
    }

    default float[] readFloatArray(int count) throws IOException {
        var results = new float[count];
        for (int i = 0; i < count; i++) {
            results[i] = readFloat();
        }
        return results;
    }

    default <T extends Enum<?>> T readEnum(Class<T> clazz) throws IOException {
        return clazz.getEnumConstants()[readVarInt()];
    }

    default Optional<String> readOptionalString() throws IOException {
        // 0 is null string.
        var len = readVarInt();
        if (len == 0) {
            return Optional.empty();
        }
        // 1 is empty string.
        var value = readString(len - 1);
        return Optional.of(value);
    }

    default OpenPrimitive readPrimitiveObject() throws IOException {
        var len = readVarInt();
        return switch (len) {
            case 0 -> OpenPrimitive.NULL;
            case 1 -> OpenPrimitive.FALSE;
            case 2 -> OpenPrimitive.TRUE;
            case 3 -> OpenPrimitive.of(readVarInt());
            case 4 -> OpenPrimitive.of(readInt());
            case 5 -> OpenPrimitive.of(readLong());
            case 6 -> OpenPrimitive.of(readFloat());
            case 7 -> OpenPrimitive.of(readDouble());
            default -> OpenPrimitive.of(readString(len - 8));
        };
    }

    default OpenVector3i readVector3i() throws IOException {
        var stream = inputStream();
        int x = stream.readInt();
        int y = stream.readInt();
        int z = stream.readInt();
        return new OpenVector3i(x, y, z);
    }

    default OpenVector3f readVector3f() throws IOException {
        var stream = inputStream();
        float x = stream.readFloat();
        float y = stream.readFloat();
        float z = stream.readFloat();
        return new OpenVector3f(x, y, z);
    }

    default OpenRectangle3i readRectangle3i() throws IOException {
        var stream = inputStream();
        int x = stream.readInt();
        int y = stream.readInt();
        int z = stream.readInt();
        int width = stream.readInt();
        int height = stream.readInt();
        int depth = stream.readInt();
        return new OpenRectangle3i(x, y, z, width, height, depth);
    }

    default OpenRectangle3f readRectangle3f() throws IOException {
        var stream = inputStream();
        float x = stream.readFloat();
        float y = stream.readFloat();
        float z = stream.readFloat();
        float width = stream.readFloat();
        float height = stream.readFloat();
        float depth = stream.readFloat();
        return new OpenRectangle3f(x, y, z, width, height, depth);
    }

    default OpenTransform3f readTransformf() throws IOException {
        var transform = new OpenTransform3f();
        transform.readFromStream(this);
        if (!transform.equals(OpenTransform3f.IDENTITY)) {
            return transform;
        }
        return OpenTransform3f.IDENTITY;
    }

    default <T extends IRegistryEntry> T readType(Function<String, T> transform) throws IOException {
        var name = readString();
        return transform.apply(name);
    }

    default SkinProperties readSkinProperties() throws IOException {
        var properties = new SkinProperties();
        properties.readFromStream(this);
        return properties;
    }

    default SkinTextureAnimation readTextureAnimation() throws IOException {
        var animation = new SkinTextureAnimation();
        animation.readFromStream(this);
        return animation;
    }

    default SkinTextureProperties readTextureProperties() throws IOException {
        var properties = new SkinTextureProperties();
        properties.readFromStream(this);
        return properties;
    }

    default CompoundTag readCompoundTag() throws IOException {
        return TagSerializer.parse(inputStream());
    }
}
