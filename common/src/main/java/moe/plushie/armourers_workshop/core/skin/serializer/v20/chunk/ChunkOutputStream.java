package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public interface ChunkOutputStream extends IOutputStream {

    default void writeFile(ChunkFile file) throws IOException {
        getFileProvider().writeItem(file, this);
    }

    ChunkContext getContext();

    default int getFileVersion() {
        return getContext().fileVersion();
    }

    default ChunkFileData getFileProvider() {
        return getContext().fileProvider();
    }

    default ChunkPaletteData getPaletteProvider() {
        return getContext().paletteProvider();
    }
}
