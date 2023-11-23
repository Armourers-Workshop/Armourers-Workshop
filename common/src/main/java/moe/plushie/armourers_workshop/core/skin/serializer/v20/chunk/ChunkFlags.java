package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public class ChunkFlags {

    private int flags;

    public ChunkFlags() {
        this(0);
    }

    public ChunkFlags(int flags) {
        this.flags = flags;
    }

    public static ChunkFlags readFromStream(IInputStream stream) throws IOException {
        return new ChunkFlags(stream.readShort());
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeShort(flags);
    }

    public void add(ChunkFlag flag) {
        flags |= 1 << flag.ordinal();
    }

    public void remove(ChunkFlag flag) {
        flags &= ~(1 << flag.ordinal());
    }

    public boolean contains(ChunkFlag flag) {
        return (flags & (1 << flag.ordinal())) != 0;
    }

    public boolean isEmpty() {
        return flags == 0;
    }
}
