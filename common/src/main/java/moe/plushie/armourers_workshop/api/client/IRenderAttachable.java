package moe.plushie.armourers_workshop.api.client;

public interface IRenderAttachable {

    void attachRenderTask(IVertexConsumer builder, Runnable runnable);
}
