package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

public interface IRenderAttachable {

    void attachRenderTask(VertexConsumer builder, Runnable runnable);
}
