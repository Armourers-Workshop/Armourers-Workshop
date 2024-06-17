package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.compatibility.AbstractShader;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferBuilder;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexMerger;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SkinVertexBufferBuilder implements IBufferSource {

    private static SkinVertexBufferBuilder MERGED_VERTEX_BUILDER;

    protected final Pipeline pipeline = new Pipeline();

    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> cachingBuilders = new HashMap<>();
    protected final HashMap<RenderType, AbstractBufferBuilder> cachingBuilders2 = new HashMap<>();

    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> pendingBuilders = new HashMap<>();
    protected final HashMap<RenderType, AbstractBufferBuilder> pendingBuilders2 = new HashMap<>();

    public SkinVertexBufferBuilder() {
    }

    public static SkinVertexBufferBuilder getBuffer(IBufferSource bufferSource) {
        attach(bufferSource, Sheets.solidBlockSheet(), SkinVertexBufferBuilder::renderSolid);
//        attach(bufferSource, Sheets.translucentCullBlockSheet(), SkinVertexBufferBuilder::renderTranslucent);
        return getInstance();
    }

    private static void attach(IBufferSource bufferSource, RenderType renderType, Runnable action) {
        var buffer = bufferSource.getBuffer(renderType);
        var attachable = ObjectUtils.safeCast(renderType, IRenderAttachable.class);
        if (attachable != null) {
            attachable.attachRenderTask(buffer, action);
        }
    }

    public static void renderSolid() {
        getInstance().endBatch();
    }

    public static void renderTranslucent() {
        getInstance().endBatch();
    }

    public static SkinVertexBufferBuilder getInstance() {
        if (MERGED_VERTEX_BUILDER == null) {
            MERGED_VERTEX_BUILDER = new SkinVertexBufferBuilder();
        }
        return MERGED_VERTEX_BUILDER;
    }

    public static void clearAllCache() {
        var builder = getInstance();
        builder.cachingBuilders.clear();
        builder.cachingBuilders2.clear();
        builder.pipeline.clear();
        SkinRenderObjectBuilder.clearAllCache();
    }

    @NotNull
    public IBufferBuilder getBuffer(@NotNull RenderType renderType) {
        var buffer = pendingBuilders2.get(renderType);
        if (buffer != null) {
            return buffer;
        }
        buffer = cachingBuilders2.computeIfAbsent(renderType, k -> new AbstractBufferBuilder(k.bufferSize()));
        buffer.begin(renderType);
        pendingBuilders2.put(renderType, buffer);
        return buffer;
    }

    public SkinRenderObjectBuilder getBuffer(@NotNull BakedSkin skin) {
        var bufferBuilder = pendingBuilders.get(skin);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = cachingBuilders.computeIfAbsent(skin, SkinRenderObjectBuilder::new);
        pendingBuilders.put(skin, bufferBuilder);
        return bufferBuilder;
    }

    @Override
    public void endBatch() {
        if (!pendingBuilders.isEmpty()) {
            for (var builder : pendingBuilders.values()) {
                builder.endBatch(pipeline);
            }
            pendingBuilders.clear();
            pipeline.end();
        }
        if (!pendingBuilders2.isEmpty()) {
            pendingBuilders2.forEach(AbstractBufferBuilder::upload);
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

        public void clear() {
            merger.clear();
        }
    }
}

