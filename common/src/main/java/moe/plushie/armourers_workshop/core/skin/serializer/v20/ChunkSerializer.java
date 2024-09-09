package moe.plushie.armourers_workshop.core.skin.serializer.v20;

import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkFlags;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkType;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;

public abstract class ChunkSerializer<V, C> {

    protected final ChunkType chunkType;
    protected final LinkedHashMap<String, Decoder<V, C>> encoders = new LinkedHashMap<>();

    public ChunkSerializer(ChunkType chunkType) {
        this.chunkType = chunkType;
        this.encoders.put(chunkType.getName(), this::read);
        this.config();
    }

    protected void config() {
    }

    protected void config(ChunkFlags flags, V value, ChunkContext context) {
    }

    public abstract V read(ChunkInputStream stream, C obj) throws IOException;

    public abstract void write(V value, C obj, ChunkOutputStream stream) throws IOException;


    public V getDefaultValue() {
        return null;
    }

    public ChunkFlags getChunkFlags(V value, ChunkContext context) {
        var flags = context.createSerializerFlags(this, value);
        config(flags, value, context);
        return flags;
    }

    public ChunkType getChunkType() {
        return chunkType;
    }

    public Decoder<V, C> createDecoder(String name) {
        return encoders.get(name);
    }

    public Encoder<V, C> createEncoder(V value, C obj, ChunkContext context) {
        if (value == null) {
            return null;
        }
        if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return null;
            }
            return this::write;
        }
        if (Objects.equals(value, getDefaultValue())) {
            return null;
        }
        return this::write;
    }


    public interface Encoder<V, C> {
        void encode(V value, C obj, ChunkOutputStream stream) throws IOException;

    }

    public interface Decoder<V, C> {
        V decode(ChunkInputStream stream, C obj) throws IOException;
    }
}
