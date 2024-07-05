package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public class ChunkCubeSelector implements ChunkVariable {

    private final int index;
    private final int count;

    private final ChunkCubeSection section;

    ChunkCubeSelector(ChunkCubeSection section, int fromIndex, int toIndex) {
        this.section = section;
        this.index = fromIndex;
        this.count = toIndex - fromIndex;
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeInt(section.getIndex() + index);
        stream.writeInt(count);
    }

    @Override
    public boolean freeze() {
        return section.isResolved();
    }


    public int getIndex() {
        return index;
    }

    public int getCount() {
        return count;
    }

    public ChunkCubeSection getSection() {
        return section;
    }
}
