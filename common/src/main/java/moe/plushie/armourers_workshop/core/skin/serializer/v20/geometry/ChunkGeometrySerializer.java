package moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry;

import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometry;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkGeometrySlice;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;

import java.io.IOException;

public abstract class ChunkGeometrySerializer {

    public abstract int stride(SkinGeometryType geometryType, int options, ChunkPaletteData palette);

    public abstract Encoder<?> encoder(SkinGeometryType geometryType);

    public abstract Decoder<?> decoder(SkinGeometryType geometryType, ChunkGeometrySlice slice);

    public interface Encoder<T extends SkinGeometry> {

        int begin(T geometry);

        void end(ChunkPaletteData palette, ChunkDataOutputStream stream) throws IOException;
    }

    public interface Decoder<T extends SkinGeometry> {

        T begin();

        default void end() {
        }
    }
}
