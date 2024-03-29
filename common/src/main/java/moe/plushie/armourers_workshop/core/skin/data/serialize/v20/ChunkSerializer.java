package moe.plushie.armourers_workshop.core.skin.data.serialize.v20;

import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkInputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkType;

import java.io.IOException;
import java.util.Collection;

public abstract class ChunkSerializer<V, T> {

    private final ChunkType chunkType;

    public ChunkSerializer(ChunkType chunkType) {
        this.chunkType = chunkType;
    }

    public abstract V read(ChunkInputStream stream, T obj) throws IOException;

    public abstract void write(V value, T obj, ChunkOutputStream stream) throws IOException;

    public boolean isChunkEmpty(V value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Collection<?>) {
            return ((Collection<?>) value).isEmpty();
        }
        return false;
    }

    public int getChunkFlags(V value, ChunkContext context) {
        return 0;
    }

    public ChunkType getChunkType() {
        return chunkType;
    }
}
