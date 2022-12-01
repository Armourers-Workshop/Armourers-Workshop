package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;

public interface IBufferBuilder {

    BufferBuilder asBufferBuilder();

    default void begin(RenderType renderType) {
        asBufferBuilder().begin(renderType.mode(), renderType.format());
    }

    IRenderedBuffer end();
}
