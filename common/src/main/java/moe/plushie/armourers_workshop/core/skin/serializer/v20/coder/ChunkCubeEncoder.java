package moe.plushie.armourers_workshop.core.skin.serializer.v20.coder;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;

import java.io.IOException;

public abstract class ChunkCubeEncoder {

    public abstract int begin(ISkinCube cube);

    public abstract void end(ChunkPaletteData palette, ChunkOutputStream stream) throws IOException;
}
