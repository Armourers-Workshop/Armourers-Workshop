package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public interface ChunkOutputStream extends IOutputStream {

    default void writeFile(ChunkFile file) throws IOException {
        fileProvider().writeItem(file, this);
    }

    ChunkContext context();

    default int fileVersion() {
        return context().fileVersion();
    }

    default ChunkFileData fileProvider() {
        return context().fileProvider();
    }

    default ChunkPaletteData paletteProvider() {
        return context().paletteProvider();
    }
}
