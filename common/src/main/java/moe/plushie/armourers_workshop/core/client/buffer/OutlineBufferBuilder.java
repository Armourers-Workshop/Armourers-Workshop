package moe.plushie.armourers_workshop.core.client.buffer;

import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import net.minecraft.client.renderer.RenderType;

public class OutlineBufferBuilder extends BufferBuilder {

    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private int alpha = 255;

    public OutlineBufferBuilder(RenderType renderType, int size) {
        super(renderType, size);
    }

    @Override
    public IVertexConsumer color(int i, int j, int k, int l) {
        return super.color(red, green, blue, alpha);
    }

    public void setDefaultColor(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}
