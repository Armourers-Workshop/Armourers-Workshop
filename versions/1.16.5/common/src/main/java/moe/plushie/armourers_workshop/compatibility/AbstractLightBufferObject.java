package moe.plushie.armourers_workshop.compatibility;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderObject;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import moe.plushie.armourers_workshop.init.platform.ClientNativeManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@Environment(value = EnvType.CLIENT)
public class AbstractLightBufferObject extends SkinRenderObject {

    public static final RenderType TYPE = RenderType.create("", createDefaultFormat(), GL11.GL_QUADS, 256, false, false, RenderType.CompositeState.builder().createCompositeState(false));
    public static final int PAGE_SIZE = 4096;
    private static final SkinCache.LRU<Integer, AbstractLightBufferObject> SHARED_LIGHTS = new SkinCache.LRU<>();
    private final int light;
    private int capacity;

    public AbstractLightBufferObject(int light) {
        super();
        this.light = light;
        this.capacity = 0;
    }

    @NotNull
    public static AbstractLightBufferObject getLightBuffer(int light) {
        return SHARED_LIGHTS.computeIfAbsent(light, AbstractLightBufferObject::new);
    }

    private static VertexFormat createDefaultFormat() {
        return new VertexFormat(ImmutableList.of(DefaultVertexFormat.ELEMENT_UV2));
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= this.capacity) {
            return;
        }
        int alignedCapacity = ((capacity / PAGE_SIZE) + 1) * PAGE_SIZE;
        IBufferBuilder builder = ClientNativeManager.createBuilderBuffer(alignedCapacity * getFormat().getVertexSize());
        builder.begin(TYPE);
        BufferBuilder bufferBuilder = builder.asBufferBuilder();
        for (int i = 0; i < alignedCapacity; ++i) {
            bufferBuilder.uv2(light).endVertex();
        }
        IRenderedBuffer renderedBuffer = builder.end();
        this.upload(renderedBuffer.vertexBuffer());
        this.capacity = alignedCapacity;
        renderedBuffer.release();
    }

    public VertexFormat getFormat() {
        return TYPE.format();
    }
}
