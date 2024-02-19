package moe.plushie.armourers_workshop.api.client;

import net.minecraft.client.renderer.RenderType;

public interface IBufferSource {

    IVertexConsumer getBuffer(RenderType renderType);

    void endBatch();
}
