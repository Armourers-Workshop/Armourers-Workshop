package moe.plushie.armourers_workshop.core.skin.data.base;

import moe.plushie.armourers_workshop.api.skin.ISkinRegistryEntry;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public interface IDataInputStream {

    static IDataInputStream of(DataInputStream stream) {
        return () -> stream;
    }

    DataInputStream stream();

    default void readFully(byte[] b) throws IOException {
        stream().readFully(b);
    }

    default void readFully(byte[] b, int off, int len) throws IOException {
        stream().readFully(b, off, len);
    }

    default byte readByte() throws IOException {
        return stream().readByte();
    }

    default boolean readBoolean() throws IOException {
        return stream().readBoolean();
    }

    default short readShort() throws IOException {
        return stream().readShort();
    }

    default int readInt() throws IOException {
        return stream().readInt();
    }

    default long readLong() throws IOException {
        return stream().readLong();
    }

    default double readDouble() throws IOException {
        return stream().readDouble();
    }

    default String readString() throws IOException {
        int size = stream().readUnsignedShort();
        return readString(size);
    }

    default String readString(int len) throws IOException {
        byte[] bytes = new byte[len];
        stream().readFully(bytes, 0, len);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    default Vector3f readVector3f() throws IOException {
        DataInputStream stream = stream();
        float x = stream.readFloat();
        float y = stream.readFloat();
        float z = stream.readFloat();
        return new Vector3f(x, y, z);
    }

    default <T extends ISkinRegistryEntry> T readType(Function<String, T> transform) throws IOException {
        String name = readString();
        return transform.apply(name);
    }

    default SkinProperties readProperties() throws IOException {
        SkinProperties properties = SkinProperties.create();
        properties.readFromStream(this);
        return properties;
    }
}
