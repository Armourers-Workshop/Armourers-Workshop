package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;

@Available("[1.21, )")
public class AbstractBufferBuilder extends AbstractVertexConsumer implements IBufferBuilder {

    private final ByteBufferBuilder buffers;
    private BufferBuilder bufferBuilder;

    public AbstractBufferBuilder(int size) {
        super(null);
        this.buffers = new ByteBufferBuilder(size);
    }

    public static void upload(RenderType renderType, AbstractBufferBuilder builder) {
        var meshData = builder.bufferBuilder.build();
        if (meshData != null) {
            renderType.draw(meshData);
        }
    }

    @Override
    public void begin(RenderType renderType) {
        var builder = new BufferBuilder(buffers, renderType.mode(), renderType.format());
        parent = builder;
        bufferBuilder = builder;
    }

    @Override
    public IRenderedBuffer end() {
        var meshData = bufferBuilder.buildOrThrow();
        return new IRenderedBuffer() {

            @Override
            public VertexFormat format() {
                return meshData.drawState().format();
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
