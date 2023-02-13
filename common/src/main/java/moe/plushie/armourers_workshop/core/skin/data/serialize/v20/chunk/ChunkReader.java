package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.ChunkSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public class ChunkReader {

    private final ChunkInputStream stream;
    private final Predicate<String> chunkFilter;

    private final ArrayList<Entry> entries = new ArrayList<>();

    public ChunkReader(ChunkInputStream stream, Predicate<String> chunkFilter) {
        this.stream = stream;
        this.chunkFilter = chunkFilter;
    }

    protected void prepare() throws IOException {
        int header = 10;
        while (true) {
            int length = stream.readInt();
            if (length == 0) {
                break;
            }
            String name = stream.readString(4);
            int flag = stream.readShort();
            if (chunkFilter != null && !chunkFilter.test(name)) {
                stream.stream().skipBytes(length - header);
                continue;
            }
            ByteBuf buffer = Unpooled.buffer(length - header);
            stream.readFully(buffer.array(), 0, length - header);
            buffer.writerIndex(length - header);
            entries.add(new Entry(name, flag, length, buffer, stream.context()));
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
            if (entry.name.equalsIgnoreCase(serializer.getChunkType().getName())) {
                iterator.remove();
                return entry.read(serializer, context);
            }
        }
        return null;
    }

    public <T> Collection<T> readAll(ChunkSerializer<T, Void> serializer) throws IOException {
        return readAll(serializer, null);
    }

    public <T, C> Collection<T> readAll(ChunkSerializer<T, C> serializer, C context) throws IOException {
        ArrayList<T> results = new ArrayList<>();
        Iterator<Entry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (entry.name.equalsIgnoreCase(serializer.getChunkType().getName())) {
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
        if (entries.size() != 0) {
            return entries;
        }
        return null;
    }

    protected static class Entry implements ChunkVariable {

        private final String name;
        private final ByteBuf buffer;

        private final int flag;
        private final int length;

        private ChunkInputStream inputStream;
        private final ChunkContext context;

        public Entry(String name, int flag, int length, ByteBuf buffer, ChunkContext context) {
            this.length = length;
            this.name = name;
            this.flag = flag;
            this.buffer = buffer;
            this.context = context;
        }

        public <T, C> T read(ChunkSerializer<T, C> serializer, C obj) throws IOException {
            if (inputStream == null) {
                inputStream = new ChunkInputStream(new DataInputStream(context.createInputStream(buffer, flag)), context, null);
            }
            return serializer.read(inputStream, obj);
        }

        @Override
        public void writeToStream(IDataOutputStream stream) throws IOException {
            stream.writeInt(length);
            stream.writeString(name, 4);
            stream.writeShort(flag);
            stream.write(buffer.array());
        }

        @Override
        public boolean freeze() {
            return true;
        }
    }
}
