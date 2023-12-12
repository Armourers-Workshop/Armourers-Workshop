package moe.plushie.armourers_workshop.core.skin.serializer.v20;

import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkFlags;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkType;

import java.io.IOException;
import java.util.Collection;

public abstract class ChunkSerializer<V, T> {

    private final ChunkType chunkType;

    public ChunkSerializer(ChunkType chunkType) {
        this.chunkType = chunkType;
    }

    public abstract V read(ChunkInputStream stream, T obj) throws IOException;

    public abstract void write(V value, T obj, ChunkOutputStream stream) throws IOException;

    public void config(ChunkFlags flags, V value, ChunkContext context) {
    }

    public boolean canWrite(V value, T obj, ChunkOutputStream stream) {
        if (value == null) {
            return false;
        }
        if (value instanceof Collection<?>) {
            return !((Collection<?>) value).isEmpty();
        }
        return true;
    }

    public V getDefaultValue(ChunkInputStream stream) throws IOException {
        return null;
    }

    public ChunkFlags getChunkFlags(V value, ChunkContext context) {
        ChunkFlags flags = new ChunkFlags();
        config(flags, value, context);
        return flags;
    }

    public ChunkType getChunkType() {
        return chunkType;
    }
}
