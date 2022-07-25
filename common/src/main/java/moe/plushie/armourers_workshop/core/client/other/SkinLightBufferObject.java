package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@Environment(value = EnvType.CLIENT)
public class SkinLightBufferObject extends SkinRenderObject {

    public static final VertexFormat FORMAT = new VertexFormat(ImmutableList.of(DefaultVertexFormat.ELEMENT_UV2));
    public static final int PAGE_SIZE = 4096;
    private static final SkinCache.LRU<Integer, SkinLightBufferObject> SHARED_LIGHTS = new SkinCache.LRU<>();
    private final int light;
    private int capacity;

    public SkinLightBufferObject(int light) {
        super();
        this.light = light;
        this.capacity = 0;
    }

    @Nonnull
    public static SkinLightBufferObject getLightBuffer(int light) {
        return SHARED_LIGHTS.computeIfAbsent(light, SkinLightBufferObject::new);
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
