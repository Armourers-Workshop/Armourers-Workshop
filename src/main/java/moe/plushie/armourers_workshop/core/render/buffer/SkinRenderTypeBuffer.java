package moe.plushie.armourers_workshop.core.render.buffer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.render.buffer.SkinVertexBuffer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class SkinRenderTypeBuffer implements IRenderTypeBuffer {

    protected final Map<RenderType, SkinVertexBuffer> compiles = new HashMap<>();
    protected final Map<RenderType, BufferBuilder> buffers = new HashMap<>();

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public IVertexBuilder getBuffer(RenderType renderType) {
        BufferBuilder buffer = buffers.get(renderType);
        if (buffer != null) {
            return buffer;
        }
        buffer = new BufferBuilder(renderType.bufferSize());
        buffer.begin(renderType.mode(), renderType.format());
        buffers.put(renderType, buffer);
        return buffer;
    }

    public void forEach(BiConsumer<RenderType, SkinVertexBuffer> action) {
        compiles.forEach(action);
    }

    public void endBatch() {
        for (Map.Entry<RenderType, BufferBuilder> entry : buffers.entrySet()) {
            BufferBuilder builder = entry.getValue();
            SkinVertexBuffer vertexBuffer = new SkinVertexBuffer(builder.getVertexFormat());
            builder.end();
            vertexBuffer.upload(builder);
            compiles.put(entry.getKey(), vertexBuffer);
        }
    }
}
