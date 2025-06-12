package moe.plushie.armourers_workshop.core.skin.serializer.io;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureAnimation;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureProperties;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.nbt.CompoundTag;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface IOutputStream {

    static IOutputStream of(OutputStream stream) {
        if (stream instanceof DataOutputStream dataOutputStream) {
            return of(dataOutputStream);
        }
        return of(new DataOutputStream(stream));
    }

    static IOutputStream of(DataOutputStream stream) {
        return () -> stream;
    }

    DataOutputStream outputStream();

    default void write(byte[] bytes) throws IOException {
        outputStream().write(bytes);
    }

    default void write(byte[] b, int off, int len) throws IOException {
        outputStream().write(b, off, len);
    }

    default void writeBytes(ByteBuf buf) throws IOException {
        writeBytes(buf, buf.readableBytes());
    }

    default void writeBytes(ByteBuf buf, int limit) throws IOException {
        buf.getBytes(0, outputStream(), limit);
    }

    default void writeByte(int v) throws IOException {
        outputStream().writeByte(v);
    }

    default void writeBoolean(boolean v) throws IOException {
        outputStream().writeBoolean(v);
    }

    default void writeShort(int v) throws IOException {
        outputStream().writeShort(v);
    }

    default void writeInt(int v) throws IOException {
        outputStream().writeInt(v);
    }

    default void writeLong(long v) throws IOException {
        outputStream().writeLong(v);
    }

    default void writeFloat(float v) throws IOException {
        outputStream().writeFloat(v);
    }

    default void writeDouble(double v) throws IOException {
        outputStream().writeDouble(v);
    }

    default void writeFixedInt(int value, int usedBytes) throws IOException {
        if (usedBytes == 4) {
            writeInt(value);
            return;
        }
        for (int i = usedBytes; i > 0; i--) {
            int ch = value >> (i - 1) * 8;
            writeByte(ch & 0xff);
        }
    }

    default void writeFixedFloat(float value, int usedBytes) throws IOException {
        writeFixedInt(Float.floatToIntBits(value), usedBytes);
    }

    default void writeString(String v) throws IOException {
        // yep, we just need write a length.
        if (v == null || v.isEmpty()) {
            outputStream().writeShort(0);
            return;
        }
        byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
        int size = bytes.length;
        if (size > 65535) {
            throw new IOException("String is over the max length allowed.");
        }
        outputStream().writeShort((short) size);
        outputStream().write(bytes);
    }

    default void writeString(String v, int len) throws IOException {
        byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
        outputStream().write(bytes, 0, len);
    }

    default void writeVarInt(int i) throws IOException {
        DataOutputStream outputStream = outputStream();
        while (true) {
            if ((i & 0xFFFFFF80) == 0) {
                outputStream.writeByte(i);
                break;
            }
            outputStream.writeByte(i & 0x7F | 0x80);
            i >>>= 7;
        }
    }

    default void writeFloatArray(float[] values) throws IOException {
        for (float value : values) {
            writeFloat(value);
        }
    }

    default void writeEnum(Enum<?> value) throws IOException {
        writeVarInt(value.ordinal());
    }

    default void writePrimitiveObject(OpenPrimitive value) throws IOException {
        var rawValue = value.rawValue();
        if (rawValue instanceof String stringValue) {
            var len = stringValue.length();
            writeVarInt(len + 8);
            writeString(stringValue, len);
        } else if (rawValue instanceof Double doubleValue) {
            writeVarInt(7);
            writeDouble(doubleValue);
        } else if (rawValue instanceof Float floatValue) {
            writeVarInt(6);
            writeFloat(floatValue);
        } else if (rawValue instanceof Long longValue) {
            writeVarInt(5);
            writeLong(longValue);
        } else if (rawValue instanceof Integer integerValue && (integerValue & 0xffff0000) != 0) {
            writeVarInt(4);
            writeInt(integerValue);
        } else if (rawValue instanceof Number numberValue) { // int/short/byte
            writeVarInt(3);
            writeVarInt(numberValue.intValue());
        } else if (rawValue instanceof Boolean booleanValue) {
            if (booleanValue) {
                writeVarInt(2);
            } else {
                writeVarInt(1);
            }
        } else if (rawValue == null) {
            writeVarInt(0);
        } else {
            throw new IOException("can't support primitive type: " + rawValue.getClass());
        }
    }

    default void writeOptionalString(String v) throws IOException {
        // 0 is null string.
        if (v == null) {
            writeVarInt(0);
            return;
        }
        // 1 is empty string.
        int len = v.length();
        writeVarInt(len + 1);
        writeString(v, len);
    }


    default void writeVector3i(IVector3i vec) throws IOException {
        var stream = outputStream();
        stream.writeInt(vec.x());
        stream.writeInt(vec.y());
        stream.writeInt(vec.z());
    }

    default void writeVector3f(IVector3f vec) throws IOException {
        var stream = outputStream();
        stream.writeFloat(vec.x());
        stream.writeFloat(vec.y());
        stream.writeFloat(vec.z());
    }

    default void writeRectangle3i(IRectangle3i rect) throws IOException {
        var stream = outputStream();
        stream.writeInt(rect.x());
        stream.writeInt(rect.y());
        stream.writeInt(rect.z());
        stream.writeInt(rect.width());
        stream.writeInt(rect.height());
        stream.writeInt(rect.depth());
    }

    default void writeRectangle3f(IRectangle3f rect) throws IOException {
        var stream = outputStream();
        stream.writeFloat(rect.x());
        stream.writeFloat(rect.y());
        stream.writeFloat(rect.z());
        stream.writeFloat(rect.width());
        stream.writeFloat(rect.height());
        stream.writeFloat(rect.depth());
    }

    default void writeTransformf(ITransform3f transform) throws IOException {
        if (transform instanceof OpenTransform3f transform1) {
            transform1.writeToStream(this);
        }
    }

    default void writeSkinProperties(SkinProperties properties) throws IOException {
        properties.writeToStream(this);
    }

    default void writeTextureAnimation(SkinTextureAnimation animation) throws IOException {
        animation.writeToStream(this);
    }

    default void writeTextureProperties(SkinTextureProperties properties) throws IOException {
        properties.writeToStream(this);
    }


    default void writeType(IRegistryEntry type) throws IOException {
        writeString(type.registryName().toString());
    }

    default void writeCompoundTag(CompoundTag value) throws IOException {
        TagSerializer.writeToStream(value, outputStream());
    }
}
