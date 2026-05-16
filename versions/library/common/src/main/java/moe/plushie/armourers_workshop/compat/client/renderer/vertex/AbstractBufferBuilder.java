package moe.plushie.armourers_workshop.compat.client.renderer.vertex;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.client.buffer.MeshData;

@Available("[21, )")
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
        this.parent = builder;
        this.bufferBuilder = builder;
    }

    @Override
    public MeshData end() {
        try (var data = bufferBuilder.buildOrThrow()) {
            var state = data.drawState();
            var format = AbstractVertexFormat.create(state.format(), state.mode());
            return new MeshData(data.vertexBuffer().duplicate(), state.vertexCount(), format);
        }
    }

    public BufferBuilder bufferBuilder() {
        return bufferBuilder;
    }
}
