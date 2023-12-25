package moe.plushie.armourers_workshop.core.skin.serializer.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.registry.IRegistryEntry;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.texture.TextureAnimation;
import moe.plushie.armourers_workshop.utils.texture.TextureProperties;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.Function;

public interface IInputStream {

    static IInputStream of(DataInputStream stream) {
        return () -> stream;
    }

    DataInputStream getInputStream();

    default void read(byte[] b) throws IOException {
        getInputStream().readFully(b);
    }

    default void read(byte[] b, int off, int len) throws IOException {
        getInputStream().readFully(b, off, len);
    }

    default void read(FloatBuffer buffer) throws IOException {
        DataInputStream stream = getInputStream();
        int position = buffer.position();
        int limit = buffer.limit();
        for (int index = position; index < limit; ++index) {
            buffer.put(index, stream.readFloat());
        }
    }

    default ByteBuf readBytes(int limit) throws IOException {
        // we can't directly create a big buffers, it's easy to be hacked.
        DataInputStream inputStream = getInputStream();
        ArrayList<byte[]> buffers = new ArrayList<>();
        int remaining = limit;
        while (remaining > 0) {
            byte[] bytes = new byte[Math.min(remaining, 16384)]; // 16k
            inputStream.readFully(bytes);
            buffers.add(bytes);
            remaining -= bytes.length;
        }
        return Unpooled.wrappedBuffer(buffers.toArray(new byte[0][]));
    }

    default byte readByte() throws IOException {
        return getInputStream().readByte();
    }

    default boolean readBoolean() throws IOException {
        return getInputStream().readBoolean();
    }

    default short readShort() throws IOException {
        return getInputStream().readShort();
    }

    default int readInt() throws IOException {
        return getInputStream().readInt();
    }

    default long readLong() throws IOException {
        return getInputStream().readLong();
    }

    default float readFloat() throws IOException {
        return getInputStream().readFloat();
    }

    default double readDouble() throws IOException {
        return getInputStream().readDouble();
    }

    default String readString() throws IOException {
        int size = getInputStream().readUnsignedShort();
        return readString(size);
    }

    default String readString(int len) throws IOException {
        if (len <= 0) {
            return "";
        }
        byte[] bytes = new byte[len];
        getInputStream().readFully(bytes, 0, len);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    default int readVarInt() throws IOException {
        DataInputStream inputStream = getInputStream();
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

    default Vector3i readVector3i() throws IOException {
        DataInputStream stream = getInputStream();
        int x = stream.readInt();
        int y = stream.readInt();
        int z = stream.readInt();
        return new Vector3i(x, y, z);
    }

    default Vector3f readVector3f() throws IOException {
        DataInputStream stream = getInputStream();
        float x = stream.readFloat();
        float y = stream.readFloat();
        float z = stream.readFloat();
        return new Vector3f(x, y, z);
    }

    default Rectangle3i readRectangle3i() throws IOException {
        DataInputStream stream = getInputStream();
        int x = stream.readInt();
        int y = stream.readInt();
        int z = stream.readInt();
        int width = stream.readInt();
        int height = stream.readInt();
        int depth = stream.readInt();
        return new Rectangle3i(x, y, z, width, height, depth);
    }

    default Rectangle3f readRectangle3f() throws IOException {
        DataInputStream stream = getInputStream();
        float x = stream.readFloat();
        float y = stream.readFloat();
        float z = stream.readFloat();
        float width = stream.readFloat();
        float height = stream.readFloat();
        float depth = stream.readFloat();
        return new Rectangle3f(x, y, z, width, height, depth);
    }

    default SkinTransform readTransformf() throws IOException {
        SkinTransform transform = new SkinTransform();
        transform.readFromStream(this);
        if (!transform.equals(SkinTransform.IDENTITY)) {
            return transform;
        }
        return SkinTransform.IDENTITY;
    }

    default <T extends IRegistryEntry> T readType(Function<String, T> transform) throws IOException {
        String name = readString();
        return transform.apply(name);
    }

    default SkinProperties readSkinProperties() throws IOException {
        SkinProperties properties = new SkinProperties();
        properties.readFromStream(this);
        return properties;
    }

    default TextureAnimation readTextureAnimation() throws IOException {
        TextureAnimation animation = new TextureAnimation();
        animation.readFromStream(this);
        return animation;
    }

    default TextureProperties readTextureProperties() throws IOException {
        TextureProperties properties = new TextureProperties();
        properties.readFromStream(this);
        return properties;
    }
}
