package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.ChunkSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public class ChunkReader {

    protected final ChunkInputStream stream;
    protected final Predicate<String> chunkFilter;

    protected final ArrayList<Entry> entries = new ArrayList<>();

    public ChunkReader(ChunkInputStream stream, Predicate<String> chunkFilter) {
        this.stream = stream;
        this.chunkFilter = chunkFilter;
    }

    protected void prepare() throws IOException {
        while (true) {
            int length = stream.readInt();
            if (length == 0) {
                break;
            }
            EntryBuilder builder = new EntryBuilder(length);
            readHeader(builder);
            if (chunkFilter != null && !chunkFilter.test(builder.name)) {
                stream.getInputStream().skipBytes(builder.getBodySize());
                readFooter(builder);
                continue;
            }
            readContent(builder);
            readFooter(builder);
            entries.add(builder.build(stream.getContext()));
        }
    }

    @Nullable
    public <T> T read(ChunkSerializer<T, Void> serializer) throws IOException {
        return read(serializer, null);
    }

    @Nullable
    public <T, C> T read(ChunkSerializer<T, C> serializer, C context) throws IOException {
        Iterator<Entry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (serializer.canRead(entry.name)) {
                iterator.remove();
                return entry.read(serializer, context);
            }
        }
        return serializer.getDefaultValue();
    }

    public <T> Collection<T> readAll(ChunkSerializer<T, Void> serializer) throws IOException {
        return readAll(serializer, null);
    }

    public <T, C> Collection<T> readAll(ChunkSerializer<T, C> serializer, C context) throws IOException {
        ArrayList<T> results = new ArrayList<>();
        Iterator<Entry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (serializer.canRead(entry.name)) {
                iterator.remove();
                T result = entry.read(serializer, context);
                if (result != null) {
                    results.add(result);
                }
            }
        }
        return results;
    }

    public Object readBlobs() throws IOException {
        if (!entries.isEmpty()) {
            return new ArrayList<>(entries);
        }
        return null;
    }

    protected void readHeader(EntryBuilder builder) throws IOException {
        builder.name = stream.readString(4);
        builder.flags = ChunkFlags.readFromStream(stream);
    }

    protected void readContent(EntryBuilder builder) throws IOException {
        builder.buffer = stream.readBytes(builder.getBodySize());
    }

    protected void readFooter(EntryBuilder builder) throws IOException {

    }

    protected static class Entry implements Chunk {

        protected final String name;
        protected final ChunkFlags flags;

        protected final int length;
        protected final ByteBuf buffer;

        protected ChunkInputStream inputStream;

        protected final ChunkContext context;
        protected final Object extra;

        public Entry(String name, ChunkFlags flags, int length, ByteBuf buffer, Object extra, ChunkContext context) {
            this.length = length;
            this.name = name;
            this.flags = flags;
            this.buffer = buffer;
            this.extra = extra;
            this.context = context;
        }

        public <T, C> T read(ChunkSerializer<T, C> serializer, C obj) throws IOException {
            if (inputStream == null) {
                inputStream = new ChunkInputStream(new DataInputStream(context.createInputStream(buffer, flags)), context, null);
            }
            return serializer.read(inputStream, name, obj);
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeBytes(buffer);
        }

        @Override
        public int getLength() {
            return length;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ChunkFlags getFlags() {
            return flags;
        }
    }

    protected static class EntryBuilder {

        String name;
        ChunkFlags flags;
        int length;

        int header = 10;
        ByteBuf buffer;
        Object extra;

        EntryBuilder(int length) {
            this.length = length;
        }

        public Entry build(ChunkContext context) {
            return new Entry(name, flags, length, buffer, extra, context);
        }

        public int getBodySize() {
            return length - header;
        }
    }
}
