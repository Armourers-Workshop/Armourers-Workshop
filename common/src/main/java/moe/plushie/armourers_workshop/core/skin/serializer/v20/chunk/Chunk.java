package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import java.io.IOException;

public interface Chunk {

    void writeToStream(ChunkOutputStream stream) throws IOException;

    int length();

    String name();

    ChunkFlags flags();
}
