package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public interface ChunkVariable {

    void writeToStream(IOutputStream stream) throws IOException;

    boolean freeze();
}
