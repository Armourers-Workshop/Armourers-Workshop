package moe.plushie.armourers_workshop.api.client;

public interface IBufferBuilder extends IVertexConsumer {

    void begin(IRenderType renderType);

    IMeshData end();
}
