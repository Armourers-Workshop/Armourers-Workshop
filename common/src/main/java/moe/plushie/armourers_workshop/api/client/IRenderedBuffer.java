package moe.plushie.armourers_workshop.api.client;

import java.nio.ByteBuffer;

public interface IRenderedBuffer {

    IVertexFormat format();

    ByteBuffer vertexBuffer();

    int vertexCount();

    default void release() {
    }
}
