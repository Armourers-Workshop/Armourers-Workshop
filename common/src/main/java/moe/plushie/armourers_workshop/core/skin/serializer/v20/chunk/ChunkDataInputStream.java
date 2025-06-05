package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOFunction;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ChunkDataInputStream implements ChunkInputStream {

    private final DataInputStream stream;
    private final Predicate<String> chunkFilter;

    private final ChunkContext context;

    public ChunkDataInputStream(DataInputStream stream, ChunkContext context, Predicate<String> chunkFilter) {
        this.stream = stream;
        this.context = context;
        this.chunkFilter = chunkFilter;
    }

    public <T> T readChunk(IOFunction<ChunkReader, T> consumer) throws IOException {
        return readChunk(ChunkReader::new, consumer);
    }

    public <T, C extends ChunkReader> T readChunk(BiFunction<ChunkDataInputStream, Predicate<String>, C> provider, IOFunction<C, T> consumer) throws IOException {
        C reader = provider.apply(this, chunkFilter);
        reader.prepare();
        return consumer.apply(reader);
    }

    @Override
    public DataInputStream inputStream() {
        return stream;
    }

    @Override
    public ChunkContext context() {
        return context;
    }
}

