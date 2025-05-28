package moe.plushie.armourers_workshop.core.client.buffer;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferBuilder;

public class BufferBuilder extends AbstractBufferBuilder {

    private final IRenderType renderType;

    public BufferBuilder(IRenderType renderType, int size) {
        super(size * 8 * renderType.format().vertexSize());
        this.renderType = renderType;
        this.begin(renderType);
    }

    public IRenderType getRenderType() {
        return renderType;
    }
}
