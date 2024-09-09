package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.ChunkSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.function.IntConsumer;

public class ChunkWriter {

    protected final ChunkOutputStream stream;

    public ChunkWriter(ChunkOutputStream stream) {
        this.stream = stream;
    }

    public <V> void write(ChunkSerializer<V, Void> serializer, @Nullable V value) throws IOException {
        write(serializer, value, null);
    }

    public <V, T> void write(ChunkSerializer<V, T> serializer, @Nullable V value, T obj) throws IOException {
        // we allow user write a null values, but it doesn't visible in the stream.
        var encoder = serializer.createEncoder(value, obj, stream.getContext());
        if (encoder == null) {
            return;
        }
        var name = serializer.getChunkType().getName();
        var flags = serializer.getChunkFlags(value, stream.getContext());
        var sum = new Sum();
        stream.writeVariable(sum);
        stream.sumTask(sum, () -> {
            writeHeader(name, flags);
            stream.compressTask(flags, () -> encoder.encode(value, obj, stream));
            writeFooter(name, flags);
        });
    }

    public void writeBlobs(Object blobs) throws IOException {
        if (blobs instanceof Collection<?>) {
            for (Object blob : (Collection<?>) blobs) {
                if (blob instanceof Chunk chunk) {
                    String name = chunk.getName();
                    ChunkFlags flags = chunk.getFlags();
                    stream.writeInt(chunk.getLength());
                    writeHeader(name, flags);
                    chunk.writeToStream(stream);
                    writeFooter(name, flags);
                }
            }
        }
    }

    protected void writeHeader(String name, ChunkFlags flags) throws IOException {
        stream.writeString(name, 4);
        flags.writeToStream(stream);
    }

    protected void writeFooter(String name, ChunkFlags flags) throws IOException {
    }

    protected static class Sum implements IntConsumer, ChunkVariable {

        private int length = 0;
        private boolean resolved = false;

        @Override
        public void accept(int length) {
            this.length = length;
            this.resolved = true;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeInt(length + 4);
        }

        @Override
        public boolean freeze() {
            return resolved;
        }
    }
}
