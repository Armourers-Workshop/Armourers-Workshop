package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

@Available("[1.21, )")
public class AbstractBufferSourceImpl {

    public static MultiBufferSource immediateSource(int size) {
        return MultiBufferSource.immediate(new ByteBufferBuilder(size));
    }

    public static MultiBufferSource bufferSource() {
        return Minecraft.getInstance().renderBuffers().bufferSource();
    }
}
