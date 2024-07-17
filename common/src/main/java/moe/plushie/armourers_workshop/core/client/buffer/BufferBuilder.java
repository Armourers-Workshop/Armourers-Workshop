package moe.plushie.armourers_workshop.core.client.buffer;

import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferBuilder;
import net.minecraft.client.renderer.RenderType;

public class BufferBuilder extends AbstractBufferBuilder {

    private final RenderType renderType;

    public BufferBuilder(RenderType renderType, int size) {
        super(size * 8 * renderType.format().getVertexSize());
        this.renderType = renderType;
    }

    public BufferBuilder begin() {
        super.begin(renderType);
        return this;
    }

    public RenderType getRenderType() {
        return renderType;
    }
}
