package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;

import java.nio.ByteBuffer;

public interface IRenderedBuffer {

    VertexFormat format();

    ByteBuffer vertexBuffer();

    int vertexCount();

    default void release() {
    }
}
