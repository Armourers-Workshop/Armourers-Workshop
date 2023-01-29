package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;

import java.io.IOException;

public interface ChunkVariable {

    void writeToStream(IDataOutputStream stream) throws IOException;

    boolean freeze();
}
