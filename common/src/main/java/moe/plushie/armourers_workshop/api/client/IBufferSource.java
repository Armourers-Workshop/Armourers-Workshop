package moe.plushie.armourers_workshop.api.client;

public interface IBufferSource {

    IVertexConsumer getBuffer(IRenderType renderType);

    void endBatch();
}
