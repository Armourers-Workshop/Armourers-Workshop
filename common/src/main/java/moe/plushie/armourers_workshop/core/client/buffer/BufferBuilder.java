package moe.plushie.armourers_workshop.core.client.buffer;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferBuilder;

public class BufferBuilder extends AbstractBufferBuilder {

    private final IRenderType renderType;

    public BufferBuilder(IRenderType renderType, int size) {
        super(size * 8 * renderType.format().byteSize());
        this.renderType = renderType;
        this.begin(renderType);
    }

    public IRenderType renderType() {
        return renderType;
    }
}
