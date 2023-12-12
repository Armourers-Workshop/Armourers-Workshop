package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.compatibility.AbstractShader;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexMerger;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SkinVertexBufferBuilder extends BufferBuilder implements MultiBufferSource {

    private static SkinVertexBufferBuilder MERGED_VERTEX_BUILDER;

    protected final Pipeline pipeline = new Pipeline();

    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> cachingBuilders = new HashMap<>();
    protected final HashMap<RenderType, BufferBuilder> cachingBuilders2 = new HashMap<>();

    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> pendingBuilders = new HashMap<>();
    protected final HashMap<RenderType, BufferBuilder> pendingBuilders2 = new HashMap<>();

    public SkinVertexBufferBuilder() {
        super(256);
    }

    public static SkinVertexBufferBuilder getBuffer(MultiBufferSource buffers) {
        attach(buffers, Sheets.solidBlockSheet(), SkinVertexBufferBuilder::renderSolid);
//        attach(buffers, Sheets.translucentCullBlockSheet(), SkinVertexBufferBuilder::renderTranslucent);
        return getInstance();
    }

    private static void attach(MultiBufferSource buffers, RenderType renderType, Runnable action) {
        VertexConsumer buffer = buffers.getBuffer(renderType);
        IRenderAttachable attachable = ObjectUtils.safeCast(renderType, IRenderAttachable.class);
        if (attachable != null) {
            attachable.attachRenderTask(buffer, action);
        }
    }

    public static void renderSolid() {
        getInstance().flush();
    }

    public static void renderTranslucent() {
        getInstance().flush();
    }

    public static SkinVertexBufferBuilder getInstance() {
        if (MERGED_VERTEX_BUILDER == null) {
            MERGED_VERTEX_BUILDER = new SkinVertexBufferBuilder();
        }
        return MERGED_VERTEX_BUILDER;
    }

    public static void clearAllCache() {
        SkinVertexBufferBuilder builder = getInstance();
        builder.cachingBuilders.clear();
        builder.cachingBuilders2.clear();
        SkinRenderObjectBuilder.clearAllCache();
    }

    @NotNull
    @Override
    public VertexConsumer getBuffer(@NotNull RenderType renderType) {
        BufferBuilder buffer = pendingBuilders2.get(renderType);
        if (buffer != null) {
            return buffer;
        }
        buffer = cachingBuilders2.computeIfAbsent(renderType, k -> new BufferBuilder(k.bufferSize()));
        buffer.begin(renderType.mode(), renderType.format());
        pendingBuilders2.put(renderType, buffer);
        return buffer;
    }

    public SkinRenderObjectBuilder getBuffer(@NotNull BakedSkin skin) {
        SkinRenderObjectBuilder bufferBuilder = pendingBuilders.get(skin);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = cachingBuilders.computeIfAbsent(skin, SkinRenderObjectBuilder::new);
        pendingBuilders.put(skin, bufferBuilder);
        return bufferBuilder;
    }

    public void flush() {
        if (!pendingBuilders.isEmpty()) {
            for (SkinRenderObjectBuilder builder : pendingBuilders.values()) {
                builder.endBatch(pipeline);
            }
            pendingBuilders.clear();
            pipeline.end();
        }
        if (!pendingBuilders2.isEmpty()) {
            pendingBuilders2.forEach((key, value) -> key.end(value, 0, 0, 0));
            pendingBuilders2.clear();
        }
    }

    public static class Pipeline {

        private final AbstractShader shader = new AbstractShader();
        private final ShaderVertexMerger merger = new ShaderVertexMerger();

        public void add(ShaderVertexObject pass) {
            merger.add(pass);
        }

        public void end() {
            if (merger.isEmpty()) {
                return;
            }
            merger.prepare();

            shader.begin();
            merger.forEach(group -> shader.apply(group, () -> group.forEach(shader::render)));
            shader.end();

            merger.reset();
        }
    }
}

