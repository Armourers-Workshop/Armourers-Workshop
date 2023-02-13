package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.ChunkSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.function.IntConsumer;

public class ChunkWriter {

    private final ChunkOutputStream stream;

    public ChunkWriter(ChunkOutputStream stream) {
        this.stream = stream;
    }

    public <V> void write(ChunkSerializer<V, Void> serializer, @Nullable V value) throws IOException {
        write(serializer, value, null);
    }

    public <V, T> void write(ChunkSerializer<V, T> serializer, @Nullable V value, T obj) throws IOException {
        // we allow user write a null values, but it doesn't visible in the stream.
        if (serializer.isChunkEmpty(value)) {
            return;
        }
        int flags = serializer.getChunkFlags(value, stream.context());
        Sum sum = new Sum();
        stream.writeVariable(sum);
        stream.sumTask(sum, () -> {
            stream.writeString(serializer.getChunkType().getName(), 4);
            stream.writeShort(flags);
            stream.compressTask(flags, () -> serializer.write(value, obj, stream));
        });
    }

    public void writeBlobs(Object blobs) throws IOException {
        if (blobs instanceof Collection<?>) {
            for (Object blob : (Collection<?>) blobs) {
                if (blob instanceof ChunkVariable) {
                    ((ChunkVariable) blob).writeToStream(stream);
                }
            }
        }
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
        public void writeToStream(IDataOutputStream stream) throws IOException {
            stream.writeInt(length + 4);
        }

        @Override
        public boolean freeze() {
            return resolved;
        }
    }
}
