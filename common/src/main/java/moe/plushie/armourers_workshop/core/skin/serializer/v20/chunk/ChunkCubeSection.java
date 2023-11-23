package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeCoders;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeEncoder;

import java.io.IOException;

public abstract class ChunkCubeSection {

    protected int index;
    protected int cubeTotal;
    protected boolean resolved;

    private final int options;
    private final ISkinCubeType cubeType;

    public ChunkCubeSection(int cubeTotal, int options, ISkinCubeType cubeType) {
        this.cubeTotal = cubeTotal;
        this.options = options;
        this.cubeType = cubeType;
    }

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    public void freeze(int index) {
        this.index = index;
        this.resolved = true;
    }

    public boolean isResolved() {
        return resolved;
    }

    public boolean isEmpty() {
        return cubeTotal == 0;
    }

    public int getIndex() {
        return index;
    }

    public int getCubeTotal() {
        return cubeTotal;
    }

    public int getCubeOptions() {
        return options;
    }

    public ISkinCubeType getCubeType() {
        return cubeType;
    }

    public static class Immutable extends ChunkCubeSection {

        public final int stride;

        private final byte[] bytes;
        private final ChunkPaletteData palette;

        public Immutable(int cubeTotal, int options, ISkinCubeType cubeType, ChunkPaletteData palette) {
            super(cubeTotal, options, cubeType);
            this.stride = ChunkCubeCoders.getStride(options, cubeType, palette);
            this.bytes = new byte[stride * cubeTotal];
            this.palette = palette;
        }

        public void readFromStream(IInputStream stream) throws IOException {
            stream.read(bytes);
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.write(bytes);
        }

        public byte[] getBytes() {
            return bytes;
        }

        public ChunkPaletteData getPalette() {
            return palette;
        }
    }

    public static class Mutable extends ChunkCubeSection {

        private final ChunkOutputStream outputStream;

        public Mutable(int options, ISkinCubeType cubeType, ChunkContext context) {
            super(0, options, cubeType);
            this.outputStream = new ChunkOutputStream(context);
        }

        public void write(ChunkCubeEncoder encoder, ChunkPaletteData palette) throws IOException {
            encoder.end(palette, outputStream);
            cubeTotal += 1;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            outputStream.transferTo(stream.getOutputStream());
        }
    }
}
