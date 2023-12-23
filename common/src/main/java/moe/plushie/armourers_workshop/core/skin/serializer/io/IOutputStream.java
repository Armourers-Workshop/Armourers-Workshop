package moe.plushie.armourers_workshop.core.skin.serializer.io;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.api.registry.IRegistryEntry;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.texture.TextureAnimation;
import moe.plushie.armourers_workshop.utils.texture.TextureProperties;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

public interface IOutputStream {

    static IOutputStream of(DataOutputStream stream) {
        return () -> stream;
    }

    DataOutputStream getOutputStream();

    default void write(byte[] bytes) throws IOException {
        getOutputStream().write(bytes);
    }

    default void write(byte[] b, int off, int len) throws IOException {
        getOutputStream().write(b, off, len);
    }

    default void writeBytes(ByteBuf buf) throws IOException {
        writeBytes(buf, buf.readableBytes());
    }

    default void writeBytes(ByteBuf buf, int limit) throws IOException {
        buf.getBytes(0, getOutputStream(), limit);
    }

    default void writeBytes(ByteBuffer buf) throws IOException {
        WritableByteChannel channel = Channels.newChannel(getOutputStream());
        channel.write(buf.duplicate());
    }

    default void writeByte(int v) throws IOException {
        getOutputStream().writeByte(v);
    }

    default void writeBoolean(boolean v) throws IOException {
        getOutputStream().writeBoolean(v);
    }

    default void writeShort(int v) throws IOException {
        getOutputStream().writeShort(v);
    }

    default void writeInt(int v) throws IOException {
        getOutputStream().writeInt(v);
    }

    default void writeLong(long v) throws IOException {
        getOutputStream().writeLong(v);
    }

    default void writeFloat(float v) throws IOException {
        getOutputStream().writeFloat(v);
    }

    default void writeDouble(double v) throws IOException {
        getOutputStream().writeDouble(v);
    }

    default void writeString(String v) throws IOException {
        // yep, we just need write a length.
        if (v == null || v.isEmpty()) {
            getOutputStream().writeShort(0);
            return;
        }
        byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
        int size = bytes.length;
        if (size > 65535) {
            throw new IOException("String is over the max length allowed.");
        }
        getOutputStream().writeShort((short) size);
        getOutputStream().write(bytes);
    }

    default void writeString(String v, int len) throws IOException {
        byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
        getOutputStream().write(bytes, 0, len);
    }

    default void writeVarInt(int i) throws IOException {
        DataOutputStream outputStream = getOutputStream();
        while (true) {
            if ((i & 0xFFFFFF80) == 0) {
                outputStream.writeByte(i);
                break;
            }
            outputStream.writeByte(i & 0x7F | 0x80);
            i >>>= 7;
        }
    }

    default void writeVector3f(IVector3f vec) throws IOException {
        DataOutputStream stream = getOutputStream();
        stream.writeFloat(vec.getX());
        stream.writeFloat(vec.getY());
        stream.writeFloat(vec.getZ());
    }

    default void writeRectangle3f(IRectangle3f rect) throws IOException {
        writeFloat(rect.getX());
        writeFloat(rect.getY());
        writeFloat(rect.getZ());
        writeFloat(rect.getWidth());
        writeFloat(rect.getHeight());
        writeFloat(rect.getDepth());
    }

    default void writeTransformf(ITransformf transform) throws IOException {
        if (transform instanceof SkinTransform) {
            ((SkinTransform) transform).writeToStream(this);
        }
    }

    default void writeSkinProperties(SkinProperties properties) throws IOException {
        properties.writeToStream(this);
    }

    default void writeTextureAnimation(TextureAnimation animation) throws IOException {
        animation.writeToStream(this);
    }

    default void writeTextureProperties(TextureProperties properties) throws IOException {
        properties.writeToStream(this);
    }


    default void writeType(IRegistryEntry type) throws IOException {
        writeString(type.getRegistryName().toString());
    }
}
