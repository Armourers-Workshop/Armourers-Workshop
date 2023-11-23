package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import java.io.IOException;

public class ChunkPartWriter extends ChunkWriter {

    protected int id = 0;

    public ChunkPartWriter(ChunkOutputStream stream) {
        super(stream);
    }

    public void prepare(int id) {
        this.id = id;
    }

    @Override
    protected void writeHeader(String name, ChunkFlags flags) throws IOException {
        super.writeHeader(name, flags);
        stream.writeInt(id);
    }
}
