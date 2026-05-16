package moe.plushie.armourers_workshop.api.client;

import java.nio.ByteBuffer;

public interface IMeshData {

    ByteBuffer bytes();

    IVertexFormat format();

    int vertexCount();

    default void retain() {
    }

    default void release() {
    }
}
