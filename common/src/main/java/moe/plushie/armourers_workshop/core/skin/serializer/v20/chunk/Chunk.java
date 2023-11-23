package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public interface Chunk {

    void writeToStream(IOutputStream stream) throws IOException;

    int getLength();

    String getName();

    ChunkFlags getFlags();
}
