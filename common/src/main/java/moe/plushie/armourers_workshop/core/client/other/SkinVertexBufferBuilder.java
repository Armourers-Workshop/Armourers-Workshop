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
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexMerger;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SkinVertexBufferBuilder implements IBufferSource {

    private static SkinVertexBufferBuilder INSTANCE;

    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> skinBufferBuilders = new HashMap<>();
    protected final HashMap<BakedSkin, SkinRenderObjectBuilder> startedSkinBufferBuilders = new HashMap<>();

    protected final HashMap<IRenderType, AbstractBufferBuilder> userBufferBuilders = new HashMap<>();
    protected final HashMap<IRenderType, AbstractBufferBuilder> startedUserBufferBuilders = new HashMap<>();

    protected final Pipeline solidPipeline = new Pipeline();
    protected final Pipeline translucentPipeline = new Pipeline();
    protected final Pipeline outlineSolidPipeline = new Pipeline();
    protected final Pipeline outlineTranslucentPipeline = new Pipeline();

    public SkinVertexBufferBuilder() {
    }

    public static SkinVertexBufferBuilder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinVertexBufferBuilder();
        }
        return INSTANCE;
    }

    public static SkinVertexBufferBuilder of(IBufferSource bufferSource) {
        var builder = getInstance();
        attach(bufferSource, SkinRenderSheets.solidBlockSheet(), builder::endBatch);
        if (bufferSource == AbstractBufferSource.outline()) {
            attach(bufferSource, SkinRenderSheets.outlineSolidBlockSheet(), builder::endOutlineBatch);
        }
        return builder;
    }

    public static SkinVertexBufferBuilder of(IBufferSource bufferSource, BakedRenderInfo renderInfo) {
        var builder = getInstance();
        attach(bufferSource, SkinRenderSheets.solidBlockSheet(), builder::endBatch);
        if (bufferSource == AbstractBufferSource.outline()) {
            attach(bufferSource, SkinRenderSheets.outlineSolidBlockSheet(), builder::endOutlineBatch);
            if (renderInfo.hasTranslucent()) {
                attach(bufferSource, SkinRenderSheets.outlineTranslucentBlockSheet(), builder::endOutlineTranslucentBatch);
            }
        } else {
            if (renderInfo.hasTranslucent()) {
                attach(bufferSource, SkinRenderSheets.glintTranslucentSheet(), builder::endTranslucentBatch);
            }
        }
        return builder;
    }

    private static void attach(IBufferSource bufferSource, IRenderType renderType, Runnable action) {
        var buffer = bufferSource.getBuffer(renderType);
        if (renderType.get() instanceof IRenderAttachable attachable) {
            attachable.attachRenderTask(buffer, action);
        }
    }

    public static void clearAllCache() {
        var builder = getInstance();
        builder.skinBufferBuilders.clear();
        builder.userBufferBuilders.clear();
        builder.solidPipeline.clear();
        builder.translucentPipeline.clear();
        builder.outlineSolidPipeline.clear();
        builder.outlineTranslucentPipeline.clear();
        ConcurrentBufferCompiler.clearAllCache();
    }

    @NotNull
    public IBufferBuilder getBuffer(@NotNull IRenderType renderType) {
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
        solidPipeline.end();
        if (!startedUserBufferBuilders.isEmpty()) {
            startedUserBufferBuilders.forEach(AbstractBufferBuilder::upload);
            startedUserBufferBuilders.clear();
        }
    }

    public void endTranslucentBatch() {
        translucentPipeline.end();
    }

    public void endOutlineBatch() {
        outlineSolidPipeline.end();
    }

    public void endOutlineTranslucentBatch() {
        outlineTranslucentPipeline.end();
    }

    private void uploadPass(ShaderVertexObject pass) {
        if (pass.isOutline()) {
            if (pass.isTranslucent()) {
                outlineTranslucentPipeline.add(pass);
            } else {
                outlineSolidPipeline.add(pass);
            }
        } else {
            if (pass.isTranslucent()) {
                translucentPipeline.add(pass);
            } else {
                solidPipeline.add(pass);
            }
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
            merger.reset();
            merger.clear();
        }
    }
}

