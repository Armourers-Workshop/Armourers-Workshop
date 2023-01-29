package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkFunction;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Predicate;

public class ChunkInputStream implements IDataInputStream {

    private final DataInputStream stream;
    private final Predicate<String> chunkFilter;

    private final ChunkContext context;

    public ChunkInputStream(DataInputStream stream, ChunkContext context, Predicate<String> chunkFilter) {
        this.stream = stream;
        this.context = context;
        this.chunkFilter = chunkFilter;
    }

    public <T> T readChunk(ChunkFunction<ChunkReader, T> consumer) throws IOException {
        ChunkReader reader = new ChunkReader(this, chunkFilter);
        reader.prepare();
        return consumer.apply(reader);
    }

    @Override
    public DataInputStream stream() {
        return stream;
    }

    public ChunkContext context() {
        return context;
    }
}

