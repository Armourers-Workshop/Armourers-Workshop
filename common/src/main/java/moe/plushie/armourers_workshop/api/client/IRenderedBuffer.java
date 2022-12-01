package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.BufferBuilder;

import java.nio.ByteBuffer;

public interface IRenderedBuffer {

    ByteBuffer vertexBuffer();

    BufferBuilder.DrawState drawState();

    default void release() {
    }
}
