package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.compatibility.AbstractShader;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexGroup;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class SkinVertexBufferBuilder extends BufferBuilder implements MultiBufferSource {

    private static SkinVertexBufferBuilder MERGED_VERTEX_BUILDER;

    protected final Pipeline pipeline = new Pipeline();

    protected final HashMap<Skin, SkinRenderObjectBuilder> cachingBuilders = new HashMap<>();
    protected final HashMap<RenderType, BufferBuilder> cachingBuilders2 = new HashMap<>();

    protected final HashMap<Skin, SkinRenderObjectBuilder> pendingBuilders = new HashMap<>();
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
        VertexConsumer ignored = buffers.getBuffer(renderType);

        IRenderAttachable builder = ObjectUtils.safeCast(renderType, IRenderAttachable.class);
        if (builder != null) {
            builder.attachRenderTask(action);
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

    public SkinRenderObjectBuilder getBuffer(@NotNull Skin skin) {
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
        private final ArrayList<ShaderVertexGroup> groups = new ArrayList<>();
        private final HashMap<RenderType, ShaderVertexGroup> pending = new HashMap<>();

        private int maxVertexCount = 0;

        public void add(ShaderVertexObject pass) {
            pending.computeIfAbsent(pass.getType(), ShaderVertexGroup::new).add(pass);
            maxVertexCount = Math.max(maxVertexCount, pass.getVertexCount());
        }

        public void end() {
            if (maxVertexCount == 0) {
                return;
            }
            setupVertexGroups();

            shader.begin();
            groups.forEach(group -> shader.apply(group, () -> group.forEach(shader::render)));
            shader.end();

            clearVertexGroups();
        }

        private void setupVertexGroups() {
            for (RenderType renderType : SkinRenderType.RENDER_ORDERING_FACES) {
                ShaderVertexGroup group = pending.get(renderType);
                if (group == null || group.isEmpty()) {
                    continue;
                }
                group.maxVertexCount = maxVertexCount;
                groups.add(group);
            }
        }

        private void clearVertexGroups() {
            groups.forEach(ShaderVertexGroup::clear);
            groups.clear();
            maxVertexCount = 0;
        }
    }
}

