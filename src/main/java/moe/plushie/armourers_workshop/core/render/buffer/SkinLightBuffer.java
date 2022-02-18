package moe.plushie.armourers_workshop.core.render.buffer;

import com.google.common.collect.ImmutableList;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SkinLightBuffer extends SkinVertexBuffer {

    public static final VertexFormat FORMAT = new VertexFormat(ImmutableList.of(DefaultVertexFormats.ELEMENT_UV2));
    public static final int PAGE_SIZE = 4096;
    private static final SkinCache.LRU<Integer, SkinLightBuffer> SHARED_LIGHTS = new SkinCache.LRU<>();
    private final int light;
    private int capacity;

    public SkinLightBuffer(int light) {
        super();
        this.light = light;
        this.capacity = 0;
    }

    @Nonnull
    public static SkinLightBuffer getLightBuffer(int light) {
        return SHARED_LIGHTS.computeIfAbsent(light, SkinLightBuffer::new);
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this.capacity) {
            return;
        }
        int alignedCapacity = ((capacity / PAGE_SIZE) + 1) * PAGE_SIZE;
        BufferBuilder builder = new BufferBuilder(alignedCapacity * getFormat().getVertexSize());
        builder.begin(GL11.GL_QUADS, getFormat());
        for (int i = 0; i < alignedCapacity; ++i) {
            builder.uv2(light).endVertex();
        }
        builder.end();
        this.upload(builder);
        this.capacity = alignedCapacity;
    }

    public VertexFormat getFormat() {
        return FORMAT;
    }
}
