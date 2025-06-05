package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;

import java.io.IOException;

public interface ChunkInputStream extends IInputStream {

    default ChunkFile readFile() throws IOException {
        return fileProvider().readItem(this);
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
