package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ChunkFileData implements ChunkVariable, ChunkCondition {

    public static final ChunkFileData EMPTY = new ChunkFileData();

    private final List<BooleanSupplier> dependencies = new ArrayList<>();
    private final List<ChunkFile> contents = new ArrayList<>();

    public ChunkFile readItem(ChunkInputStream stream) throws IOException {
        // TODO: remove in the future (22-refactor-file).
        if (stream.fileVersion() < 22) {
            var size = stream.readInt();
            var buffer = stream.readBytes(size);
            return ChunkFile.image(null, buffer); // in the older version, only image.
        }
        var id = stream.readVarInt();
        return contents.get(id);
    }

    public void writeItem(ChunkFile item, ChunkOutputStream stream) throws IOException {
        int id = contents.size();
        contents.add(item);
        stream.writeVarInt(id);
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        var size = stream.readVarInt();
        for (int i = 0; i < size; i++) {
            var type = stream.readVarInt();
            var name = stream.readOptionalString();
            var properties = stream.readSkinProperties();
            var byteSize = stream.readVarInt();
            var bytes = stream.readBytes(byteSize);
            contents.add(new ChunkFile(type, name.orElse(null), properties, bytes));
        }
    }

    @Override
    public void writeToStream(ChunkOutputStream stream) throws IOException {
        stream.writeVarInt(contents.size());
        for (var content : contents) {
            stream.writeVarInt(content.type());
            stream.writeOptionalString(content.name());
            stream.writeSkinProperties(content.properties());
            var buf = content.bytes().slice();
            stream.writeVarInt(buf.readableBytes());
            stream.writeBytes(buf);
        }
    }

    @Override
    public boolean freeze() {
        for (var dependency : dependencies) {
            if (!dependency.getAsBoolean()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChunkFileData that)) return false;
        return contents.equals(that.contents);
    }

    @Override
    public int hashCode() {
        return contents.hashCode();
    }

    @Override
    public ChunkConditionResult result() {
        if (!freeze()) {
            return ChunkConditionResult.PENDING;
        }
        if (contents.isEmpty()) {
            return ChunkConditionResult.FAILURE;
        }
        return ChunkConditionResult.PASS;
    }

    public void addDependency(BooleanSupplier dependency) {
        this.dependencies.add(dependency);
    }
}
