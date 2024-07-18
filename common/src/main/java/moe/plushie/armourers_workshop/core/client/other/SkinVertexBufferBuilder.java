package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.compatibility.AbstractShader;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferBuilder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderSheet;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexMerger;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SkinVertexBufferBuilder implements IBufferSource {

    private static SkinVertexBufferBuilder INSTANCE;

    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> skinBufferBuilders = new HashMap<>();
    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> startedSkinBufferBuilders = new HashMap<>();

    protected final HashMap<RenderType, AbstractBufferBuilder> userBufferBuilders = new HashMap<>();
    protected final HashMap<RenderType, AbstractBufferBuilder> startedUserBufferBuilders = new HashMap<>();

    protected final Pipeline pipeline = new Pipeline();
    protected final Pipeline translucentPipeline = new Pipeline();
    protected final Pipeline outlinePipeline = new Pipeline();

    public SkinVertexBufferBuilder() {
    }

    public static SkinVertexBufferBuilder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinVertexBufferBuilder();
        }
        return INSTANCE;
    }

    public static SkinVertexBufferBuilder getBuffer(IBufferSource bufferSource) {
        var builder = getInstance();
        attach(bufferSource, AbstractRenderSheet.solidBlockSheet(), builder::endBatch);
        //attach(bufferSource, AbstractRenderSheet.translucentBlockSheet(), builder::endTranslucentBatch);
        if (bufferSource == AbstractBufferSource.outline()) {
            attach(bufferSource, AbstractRenderSheet.outlineBlockSheet(), builder::endOutlineBatch);
        }
        return builder;
    }

    private static void attach(IBufferSource bufferSource, RenderType renderType, Runnable action) {
        var buffer = bufferSource.getBuffer(renderType);
        var attachable = ObjectUtils.safeCast(renderType, IRenderAttachable.class);
        if (attachable != null) {
            attachable.attachRenderTask(buffer, action);
        }
    }

    public static void beginFrame() {
    }

    public static void endFrame() {
        // when end frame rendering,
        // we will clear unused passes to avoid issue.
        var builder = getInstance();
        builder.pipeline.discard();
        builder.translucentPipeline.discard();
        builder.outlinePipeline.discard();
    }

    public static void clearAllCache() {
        var builder = getInstance();
        builder.skinBufferBuilders.clear();
        builder.userBufferBuilders.clear();
        builder.pipeline.clear();
        builder.translucentPipeline.clear();
        builder.outlinePipeline.clear();
        ConcurrentBufferCompiler.clearAllCache();
    }

    @NotNull
    public IBufferBuilder getBuffer(@NotNull RenderType renderType) {
        var buffer = startedUserBufferBuilders.get(renderType);
        if (buffer != null) {
            return buffer;
        }
        buffer = userBufferBuilders.computeIfAbsent(renderType, k -> new AbstractBufferBuilder(k.bufferSize()));
        buffer.begin(renderType);
        startedUserBufferBuilders.put(renderType, buffer);
        return buffer;
    }

    public SkinRenderObjectBuilder getBuffer(@NotNull BakedSkin skin) {
        var bufferBuilder = startedSkinBufferBuilders.get(skin);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = skinBufferBuilders.computeIfAbsent(skin, SkinRenderObjectBuilder::new);
        startedSkinBufferBuilders.put(skin, bufferBuilder);
        return bufferBuilder;
    }

    @Override
    public void endBatch() {
        if (!startedSkinBufferBuilders.isEmpty()) {
            for (var builder : startedSkinBufferBuilders.values()) {
                builder.endBatch(this::uploadPass);
            }
            startedSkinBufferBuilders.clear();
        }
        pipeline.end();
        if (!startedUserBufferBuilders.isEmpty()) {
            startedUserBufferBuilders.forEach(AbstractBufferBuilder::upload);
            startedUserBufferBuilders.clear();
        }
    }

    public void endTranslucentBatch() {
        // follow the solid rendering task.
        translucentPipeline.end();
    }

    public void endOutlineBatch() {
        // follow the solid rendering task.
        outlinePipeline.end();
    }

    private void uploadPass(ShaderVertexObject pass) {
        if (pass.isOutline()) {
            outlinePipeline.add(pass);
        } else {
            pipeline.add(pass);
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

        public void discard() {
            if (merger.isEmpty()) {
                return;
            }
            //ModLog.debug("discard unused render task {}", merger.size());
            merger.reset();
        }
    }
}

