package moe.plushie.armourers_workshop.core.client.buffer;


import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;

import java.nio.ByteBuffer;

public class MeshData extends ReferenceCounted implements IMeshData {

    protected final ByteBuffer bytes;

    protected final IVertexFormat format;

    protected final int vertexCount;

    public MeshData(ByteBuffer bytes, int vertexCount, IVertexFormat format) {
        this.bytes = bytes;
        this.vertexCount = vertexCount;
        this.format = format;
    }

    @Override
    public ByteBuffer bytes() {
        return bytes;
    }

    @Override
    public IVertexFormat format() {
        return format;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }
}
