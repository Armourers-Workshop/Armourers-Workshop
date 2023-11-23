package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.utils.SliceRandomlyAccessor;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public abstract class ChunkCubeSlice extends SkinCube implements SliceRandomlyAccessor.Provider<SkinCube> {

    protected int readerIndex = 0;

    protected final int startIndex;
    protected final int endIndex;

    protected final int base;
    protected final int stride;

    public ChunkCubeSlice(int startIndex, int endIndex, int baseIndex, ChunkCubeSection.Immutable section) {
        this.pos = new Vector3i(0, 0, 0); // we need change the pos member.
        this.type = section.getCubeType();
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.base = baseIndex;
        this.stride = section.stride;
    }

    @Override
    public SkinCube get(int index) {
        int newReaderIndex = stride * (base + index);
        if (this.readerIndex != newReaderIndex) {
            this.readerIndex = newReaderIndex;
            this.reset();
        }
        return this;
    }

    @Override
    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex;
    }

    protected abstract void reset();
}
