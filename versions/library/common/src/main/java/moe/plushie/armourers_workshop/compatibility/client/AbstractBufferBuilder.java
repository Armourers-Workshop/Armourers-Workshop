package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;

import java.nio.ByteBuffer;

@Available("[1.21, )")
public class AbstractBufferBuilder extends AbstractVertexConsumer implements IBufferBuilder {

    private final ByteBufferBuilder buffers;
    private BufferBuilder bufferBuilder;

    public AbstractBufferBuilder(int size) {
        super(null);
        this.buffers = new ByteBufferBuilder(size);
    }

    public static void upload(IRenderType renderType, AbstractBufferBuilder builder) {
        var renderType1 = renderType.get();
        var meshData = builder.bufferBuilder.build();
        if (meshData != null) {
            renderType1.draw(meshData);
        }
    }

    @Override
    public void begin(IRenderType renderType) {
        var renderType1 = renderType.get();
        var builder = new BufferBuilder(buffers, renderType1.mode(), renderType1.format());
        parent = builder;
        bufferBuilder = builder;
    }

    @Override
    public IRenderedBuffer end() {
        var meshData = bufferBuilder.buildOrThrow();
        var format = AbstractVertexFormat.of(meshData.drawState().format());
        return new IRenderedBuffer() {

            @Override
            public IVertexFormat format() {
                return format;
            }

            @Override
            public ByteBuffer vertexBuffer() {
                return meshData.vertexBuffer();
            }

            @Override
            public int vertexCount() {
                return meshData.drawState().vertexCount();
            }

            @Override
            public void release() {
                meshData.close();
            }
        };
    }

    public BufferBuilder bufferBuilder() {
        return bufferBuilder;
    }
}
