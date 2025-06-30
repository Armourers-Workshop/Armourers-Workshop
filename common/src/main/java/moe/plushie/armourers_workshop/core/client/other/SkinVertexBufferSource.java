package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferBuilder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractShader;
import moe.plushie.armourers_workshop.core.client.bake.BakedRenderInfo;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.shader.Shader;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexMerger;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SkinVertexBufferSource implements IBufferSource {

    private static final SkinVertexBufferSource INSTANCE = new SkinVertexBufferSource();

    protected final HashMap<BakedSkin, SkinVertexBufferBuilder> skinBufferBuilders = new HashMap<>();
    protected final HashMap<BakedSkin, SkinVertexBufferBuilder> startedSkinBufferBuilders = new HashMap<>();

    protected final HashMap<IRenderType, AbstractBufferBuilder> bufferBuilders = new HashMap<>();
    protected final HashMap<IRenderType, AbstractBufferBuilder> startedBufferBuilders = new HashMap<>();

    protected final Pipeline solidPipeline = new Pipeline();
    protected final Pipeline outlinePipeline = new Pipeline();
    protected final Pipeline translucentPipeline = new Pipeline();

    public static SkinVertexBufferSource of(IBufferSource bufferSource) {
        attach(bufferSource, SkinRenderSheets.solidBlockSheet(), INSTANCE::flush);
        return INSTANCE;
    }

    public static SkinVertexBufferSource of(IBufferSource bufferSource, boolean shouldRenderOutline, BakedRenderInfo renderInfo) {
        attach(bufferSource, SkinRenderSheets.solidBlockSheet(), INSTANCE::endBatch);
        if (renderInfo.hasTranslucent()) {
            attach(bufferSource, SkinRenderSheets.glintTranslucentSheet(), INSTANCE::endTranslucentBatch);
        }
        if (shouldRenderOutline) {
            attach(AbstractBufferSource.outline(), SkinRenderSheets.outlineSolidBlockSheet(), INSTANCE::endOutlineBatch);
        }
        return INSTANCE;
    }

    public static void clearAllCache() {
        INSTANCE.clean();
        ConcurrentBufferCompiler.clearAllCache();
    }

    private static void attach(IBufferSource bufferSource, IRenderType renderType, Runnable action) {
        var buffer = bufferSource.getBuffer(renderType);
        if (renderType.get() instanceof IRenderAttachable attachable) {
            attachable.attachRenderTask(buffer, action);
        }
    }

    @NotNull
    public IBufferBuilder getBuffer(@NotNull IRenderType renderType) {
        var buffer = startedBufferBuilders.get(renderType);
        if (buffer != null) {
            return buffer;
        }
        buffer = bufferBuilders.computeIfAbsent(renderType, it -> new AbstractBufferBuilder(it.bufferSize()));
        buffer.begin(renderType);
        startedBufferBuilders.put(renderType, buffer);
        return buffer;
    }

    public SkinVertexBufferBuilder getBuffer(@NotNull BakedSkin skin) {
        var bufferBuilder = startedSkinBufferBuilders.get(skin);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = skinBufferBuilders.computeIfAbsent(skin, it -> new SkinVertexBufferBuilder());
        startedSkinBufferBuilders.put(skin, bufferBuilder);
        return bufferBuilder;
    }

    public void clean() {
        skinBufferBuilders.clear();
        bufferBuilders.clear();
        startedSkinBufferBuilders.clear();
        startedBufferBuilders.clear();
        solidPipeline.clear();
        outlinePipeline.clear();
        translucentPipeline.clear();
    }

    public void upload(ShaderVertexObject pass) {
        if (pass.isOutline()) {
            outlinePipeline.add(pass);
        } else if (pass.isTranslucent()) {
            translucentPipeline.add(pass);
        } else {
            solidPipeline.add(pass);
        }
    }

    public void flush() {
        endBatch();
        endTranslucentBatch();
    }

    @Override
    public void endBatch() {
        if (!startedSkinBufferBuilders.isEmpty()) {
            for (var builder : startedSkinBufferBuilders.values()) {
                builder.endBatch(this::upload);
            }
            startedSkinBufferBuilders.clear();
        }
        solidPipeline.end();
        if (!startedBufferBuilders.isEmpty()) {
            startedBufferBuilders.forEach(AbstractBufferBuilder::upload);
            startedBufferBuilders.clear();
        }
    }

    public void endOutlineBatch() {
        outlinePipeline.end();
    }

    public void endTranslucentBatch() {
        translucentPipeline.end();
    }

    public static class Pipeline {

        private final Shader shader = new AbstractShader();
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
            merger.reset();
            merger.clear();
        }
    }
}

