package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.compat.core.data.serializer.AbstractTagDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("unused")
public class TagSerializer extends AbstractTagDataSerializer {

    public TagSerializer() {
        super(new CompoundTag(), SerializationContext.EMPTY);
    }

    public TagSerializer(SerializationContext context) {
        super(new CompoundTag(), context);
    }

    public TagSerializer(CompoundTag tag) {
        super(tag, SerializationContext.EMPTY);
    }

    public TagSerializer(CompoundTag tag, SerializationContext context) {
        super(tag, context);
    }

    public TagSerializer(InputStream inputStream) throws IOException {
        super(parse(inputStream), SerializationContext.EMPTY);
    }


    public static void writeToStream(IDataSerializable.Immutable value, OutputStream outputStream) throws IOException {
        var serializer = new TagSerializer();
        value.serialize(serializer);
        writeToStream(serializer.tag(), outputStream);
    }

    public static void writeToStream(CompoundTag compoundTag, OutputStream outputStream) throws IOException {
        try (var dataOutputStream = new DataOutputStream(outputStream)) {
            NbtIo.write(compoundTag, dataOutputStream);
        }
    }

    public static CompoundTag parse(InputStream inputStream) throws IOException {
        try (var datainputstream = new DataInputStream(inputStream)) {
            return NbtIo.read(datainputstream);
        }
    }

    public static CompoundTag parse(String contents) {
        try {
            return TagParser.parseCompoundFully(contents);
        } catch (Exception e) {
            return new CompoundTag();
        }
    }
}
