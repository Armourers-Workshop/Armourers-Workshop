package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.VertexFormatElement;

import java.util.function.Supplier;

public interface IVertexElement extends Supplier<VertexFormatElement> {

    void setupBufferState(int index, int stride, long pointer);

    void clearBufferState(int index);

    int byteSize();
}
