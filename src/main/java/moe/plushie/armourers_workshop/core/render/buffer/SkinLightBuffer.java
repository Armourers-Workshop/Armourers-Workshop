package moe.plushie.armourers_workshop.core.render.buffer;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class SkinLightBuffer extends SkinVertexBuffer {

    public static final VertexFormat FORMAT = new VertexFormat(ImmutableList.of(DefaultVertexFormats.ELEMENT_UV2));
    public static final int PAGE_SIZE = 4096;

    private final int light;
    private int capacity;

    public SkinLightBuffer(int light) {
        super(FORMAT);
        this.light = light;
        this.capacity = 0;
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
}
