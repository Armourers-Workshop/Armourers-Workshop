package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializers;

import java.io.IOException;

public abstract class ChunkGeometrySection {

    protected int index;
    protected int geometryTotal;
    protected boolean resolved;

    private final int geometryOptions;
    private final SkinGeometryType geometryType;

    public ChunkGeometrySection(int geometryTotal, int geometryOptions, SkinGeometryType geometryType) {
        this.geometryTotal = geometryTotal;
        this.geometryOptions = geometryOptions;
        this.geometryType = geometryType;
    }

    public abstract void writeToStream(ChunkOutputStream stream) throws IOException;

    public void freeze(int index) {
        this.index = index;
        this.resolved = true;
    }

    public boolean isResolved() {
        return resolved;
    }

    public boolean isEmpty() {
        return geometryTotal == 0;
    }

    public int getIndex() {
        return index;
    }

    public int getGeometryTotal() {
        return geometryTotal;
    }

    public int getGeometryOptions() {
        return geometryOptions;
    }

    public SkinGeometryType getGeometryType() {
        return geometryType;
    }

    public static class Immutable extends ChunkGeometrySection {

        public final int stride;

        private final byte[] bytes;
        private final ChunkPaletteData palette;

        public Immutable(int geometryTotal, int options, SkinGeometryType geometryType, ChunkPaletteData palette) {
            super(geometryTotal, options, geometryType);
            this.stride = ChunkGeometrySerializers.getStride(geometryType, options, palette);
            this.bytes = new byte[stride * geometryTotal];
            this.palette = palette;
        }

        public void readFromStream(ChunkInputStream stream) throws IOException {
            stream.read(bytes);
        }

        @Override
        public void writeToStream(ChunkOutputStream stream) throws IOException {
            stream.write(bytes);
        }

        public byte[] getBytes() {
            return bytes;
        }

        public ChunkPaletteData getPalette() {
            return palette;
        }
    }

    public static class Mutable extends ChunkGeometrySection {

        private final ChunkDataOutputStream outputStream;

        public Mutable(int options, SkinGeometryType geometryType, ChunkContext context) {
            super(0, options, geometryType);
            this.outputStream = new ChunkDataOutputStream(context);
        }

        public void write(ChunkGeometrySerializer.Encoder<?> encoder, ChunkPaletteData palette) throws IOException {
            encoder.end(palette, outputStream);
            geometryTotal += 1;
        }

        @Override
        public void writeToStream(ChunkOutputStream stream) throws IOException {
            outputStream.transferTo(stream.getOutputStream());
        }
    }
}
