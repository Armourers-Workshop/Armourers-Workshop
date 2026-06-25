package moe.plushie.armourers_workshop.api.client;

public interface IVertexBuffer {

    IMeshData slice(int offset, int vertexCount, IVertexFormat format);

    void retain();

    void release();
}
