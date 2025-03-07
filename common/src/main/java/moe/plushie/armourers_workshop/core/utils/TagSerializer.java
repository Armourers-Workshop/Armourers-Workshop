package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TagSerializer extends AbstractDataSerializer {

    public TagSerializer() {
        super(new CompoundTag(), null);
    }

    public TagSerializer(CompoundTag tag) {
        super(tag, null);
    }

    public TagSerializer(CompoundTag tag, @Nullable Object context) {
        super(tag, context);
    }

    public TagSerializer(InputStream inputStream) throws IOException {
        this(parse(inputStream));
    }

    public static void writeToStream(IDataSerializable.Immutable value, OutputStream outputStream) throws IOException {
        var serializer = new TagSerializer();
        value.serialize(serializer);
        writeToStream(serializer.getTag(), outputStream);
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
            return TagParser.parseTag(contents);
        } catch (Exception e) {
            return new CompoundTag();
        }
    }

    public CompoundTag getTag() {
        return tag;
    }
}
