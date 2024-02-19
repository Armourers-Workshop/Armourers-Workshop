package moe.plushie.armourers_workshop.api.client;

import net.minecraft.client.renderer.RenderType;

public interface IBufferBuilder extends IVertexConsumer {

    void begin(RenderType renderType);

    IRenderedBuffer end();
}
