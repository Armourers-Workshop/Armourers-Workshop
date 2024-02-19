package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;

@Available("[1.19, )")
public class AbstractBufferBuilder extends AbstractVertexConsumer implements IBufferBuilder {

    private final BufferBuilder bufferBuilder;

    public AbstractBufferBuilder(int size) {
        this(new BufferBuilder(size));
    }

    public AbstractBufferBuilder(BufferBuilder bufferBuilder) {
        super(bufferBuilder);
        this.bufferBuilder = bufferBuilder;
    }

    @Override
    public void begin(RenderType renderType) {
        bufferBuilder.begin(renderType.mode(), renderType.format());
    }

    @Override
    public IRenderedBuffer end() {
        BufferBuilder.RenderedBuffer buffer = bufferBuilder.end();
        return new IRenderedBuffer() {
            @Override
            public ByteBuffer vertexBuffer() {
                return buffer.vertexBuffer();
            }

            @Override
            public BufferBuilder.DrawState drawState() {
                return buffer.drawState();
            }

            @Override
            public void release() {
                buffer.release();
            }
        };
    }
}
