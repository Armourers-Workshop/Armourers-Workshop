package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.v20.ChunkSerializer;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.function.IntConsumer;

public class ChunkWriter {

    protected final ChunkDataOutputStream stream;

    public ChunkWriter(ChunkDataOutputStream stream) {
        this.stream = stream;
    }

    public <V> void write(ChunkSerializer<V, Void> serializer, @Nullable V value) throws IOException {
        write(serializer, value, null);
    }

    public <V, T> void write(ChunkSerializer<V, T> serializer, @Nullable V value, T context) throws IOException {
        var encoder = serializer.createEncoder(value, context, stream.context());
        var condition = Condition.of(value, serializer.defaultValue());
        stream.ifTask(condition, () -> {
            var name = serializer.chunkType().serializedName();
            var flags = serializer.getChunkFlags(value, stream.context());
            var sum = new Sum();
            stream.writeVariable(sum);
            stream.sumTask(sum, () -> {
                writeHeader(name, flags);
                stream.compressTask(flags, () -> encoder.encode(value, context, stream));
                writeFooter(name, flags);
            });
        });
    }

    public void writeBlobs(Object blobs) throws IOException {
        if (blobs instanceof Collection<?> allBlobs) {
            for (var blob : allBlobs) {
                if (blob instanceof Chunk chunk) {
                    var name = chunk.name();
                    var flags = chunk.flags();
                    stream.writeInt(chunk.length());
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

    private static class Sum implements IntConsumer, ChunkVariable {

        private int length = 0;
        private boolean resolved = false;

        @Override
        public void accept(int length) {
            this.length = length;
            this.resolved = true;
        }

        @Override
        public void writeToStream(ChunkOutputStream stream) throws IOException {
            stream.writeInt(length + 4);
        }

        @Override
        public boolean freeze() {
            return resolved;
        }
    }

    private static class Condition implements ChunkCondition {

        private static final Condition PASS = new Condition(ChunkConditionResult.PASS);
        private static final Condition FAILURE = new Condition(ChunkConditionResult.FAILURE);

        private final ChunkConditionResult result;

        public Condition(ChunkConditionResult result) {
            this.result = result;
        }

        public static <V> ChunkCondition of(V value, V defaultValue) {
            // using user condition.
            if (value instanceof ChunkCondition condition) {
                return condition;
            }
            // check collection value.
            if (value instanceof Collection<?> collection && collection.isEmpty()) {
                return FAILURE;
            }
            // check empty value.
            if (value == null || Objects.equals(value, defaultValue)) {
                return FAILURE;
            }
            return PASS;
        }

        @Override
        public ChunkConditionResult result() {
            return result;
        }
    }
}
