package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import java.io.IOException;

public class ChunkGeometrySelector implements ChunkVariable {

    private final int index;
    private final int count;

    private final ChunkGeometrySection section;

    ChunkGeometrySelector(ChunkGeometrySection section, int fromIndex, int toIndex) {
        this.section = section;
        this.index = fromIndex;
        this.count = toIndex - fromIndex;
    }

    @Override
    public void writeToStream(ChunkOutputStream stream) throws IOException {
        stream.writeInt(section.index() + index);
        stream.writeInt(count);
    }

    @Override
    public boolean freeze() {
        return section.isResolved();
    }


    public int index() {
        return index;
    }

    public int count() {
        return count;
    }

    public ChunkGeometrySection section() {
        return section;
    }
}
