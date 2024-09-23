package moe.plushie.armourers_workshop.core.skin.serializer.v20.coder;

import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSection;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSelector;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSlice;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public abstract class ChunkCubeDecoder extends ChunkCubeSlice {

    protected final int faceCount;

    protected final byte[] bytes;
    protected final ChunkPaletteData palette;

    public ChunkCubeDecoder(int startIndex, int endIndex, ChunkCubeSelector selector, ChunkCubeSection.Immutable section) {
        super(startIndex, endIndex, selector.getIndex(), section);
        this.bytes = section.getBytes();
        this.palette = section.getPalette();
        this.faceCount = section.getCubeOptions() & 0x0F;
    }

    protected byte getByte(int offset) {
        return bytes[readerIndex + offset];
    }

    protected int getInt(int offset) {
        int ch1 = getByte(offset + 0) & 0xff;
        int ch2 = getByte(offset + 1) & 0xff;
        int ch3 = getByte(offset + 2) & 0xff;
        int ch4 = getByte(offset + 3) & 0xff;
        return ((ch1 << 24) | (ch2 << 16) | (ch3 << 8) | ch4);
    }

    protected float getFloat(int offset) {
        return Float.intBitsToFloat(getInt(offset));
    }

    protected Vector3f getVector3f(int offset) {
        float x = getFloat(offset);
        float y = getFloat(offset + 4);
        float z = getFloat(offset + 8);
        if (x == 0 && y == 0 && z == 0) {
            return Vector3f.ZERO;
        }
        if (x == 1 && y == 1 && z == 1) {
            return Vector3f.ONE;
        }
        return new Vector3f(x, y, z);
    }
}
