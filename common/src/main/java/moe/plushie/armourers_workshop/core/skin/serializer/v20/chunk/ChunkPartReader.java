package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Predicate;

public class ChunkPartReader extends ChunkReader {

    protected int id = 0;
    protected final LinkedHashMap<Object, ArrayList<Entry>> mapping = new LinkedHashMap<>();

    public ChunkPartReader(ChunkInputStream stream, Predicate<String> chunkFilter) {
        super(stream, chunkFilter);
    }

    @Override
    protected void prepare() throws IOException {
        super.prepare();
        for (Entry entry : entries) {
            mapping.computeIfAbsent(entry.extra, k -> new ArrayList<>()).add(entry);
        }
    }

    public void prepare(int id) {
        this.id = id;
        this.entries.clear();
        this.entries.addAll(mapping.getOrDefault(id, new ArrayList<>()));
    }

    @Override
    protected void readHeader(EntryBuilder builder) throws IOException {
        super.readHeader(builder);
        builder.extra = stream.readInt();
        builder.header += 4;
    }
}
