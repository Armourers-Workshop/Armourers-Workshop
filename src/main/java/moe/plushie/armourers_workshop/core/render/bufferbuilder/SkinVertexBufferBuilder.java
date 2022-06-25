package moe.plushie.armourers_workshop.core.render.bufferbuilder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SkinVertexBufferBuilder extends BufferBuilder implements IRenderTypeBuffer {

    public static final RenderType MERGER = new Merger("skin_merger", DefaultVertexFormats.POSITION, GL11.GL_QUADS, 256);

    protected final Pipeline pipeline = new Pipeline();

    protected final HashMap<Skin, SkinRenderObjectBuilder> cachingBuilders = new HashMap<>();
    protected final HashMap<RenderType, BufferBuilder> cachingBuilders2 = new HashMap<>();

    protected final HashMap<Skin, SkinRenderObjectBuilder> pendingBuilders = new HashMap<>();

    protected final HashMap<RenderType, BufferBuilder> pendingBuilders2 = new HashMap<>();

    public SkinVertexBufferBuilder() {
        super(256);
    }

    public static SkinVertexBufferBuilder getBuffer(IRenderTypeBuffer buffers) {
        IVertexBuilder builder = buffers.getBuffer(MERGER);
        if (builder instanceof SkinVertexBufferBuilder) {
            return (SkinVertexBufferBuilder) builder;
        }
        return new SkinVertexBufferBuilder();
    }

    public static void clearAllCache() {
        SkinVertexBufferBuilder builder = getBuffer(Minecraft.getInstance().renderBuffers().bufferSource());
        builder.cachingBuilders.clear();
        builder.cachingBuilders2.clear();
    }

    @Nonnull
    @Override
    public IVertexBuilder getBuffer(@Nonnull RenderType renderType) {
        BufferBuilder buffer = pendingBuilders2.get(renderType);
        if (buffer != null) {
            return buffer;
        }
        buffer = cachingBuilders2.computeIfAbsent(renderType, k -> new BufferBuilder(k.bufferSize()));
        buffer.begin(renderType.mode(), renderType.format());
        pendingBuilders2.put(renderType, buffer);
        return buffer;
    }

    public SkinRenderObjectBuilder getBuffer(@Nonnull Skin skin) {
        SkinRenderObjectBuilder bufferBuilder = pendingBuilders.get(skin);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = cachingBuilders.computeIfAbsent(skin, SkinRenderObjectBuilder::new);
        pendingBuilders.put(skin, bufferBuilder);
        return bufferBuilder;
    }

    @Override
    public void end() {
        super.end();
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

    public abstract static class Pass {

        public abstract int getLightmap();

        public abstract int getVertexOffset();

        public abstract int getVertexCount();

        public abstract Matrix4f getMatrix();

        public abstract ISkinPartType getPartType();

        public abstract RenderType getRenderType();

        public abstract float getPolygonOffset();

        public abstract SkinRenderObject getVertexBuffer();

        public void render(SkinRenderType renderType, int index, int maxVertexCount) {
            SkinLightBufferObject lightBuffer = null;
            SkinRenderObject vertexBuffer = getVertexBuffer();

            float polygonOffset = getPolygonOffset();
            if (polygonOffset != 0) {
                // https://sites.google.com/site/threejstuts/home/polygon_offset
                // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
                // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
                // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
                RenderSystem.polygonOffset(0, polygonOffset * -1);
                RenderSystem.enablePolygonOffset();
            }

            if (renderType.usesLight()) {
                lightBuffer = SkinLightBufferObject.getLightBuffer(getLightmap());
                lightBuffer.ensureCapacity(maxVertexCount);
                lightBuffer.bind();
                lightBuffer.getFormat().setupBufferState(0L);
            }

            vertexBuffer.bind();
            renderType.format().setupBufferState(getVertexOffset());

            vertexBuffer.draw(getMatrix(), renderType.mode(), getVertexCount());

            renderType.format().clearBufferState();

            if (lightBuffer != null) {
                lightBuffer.getFormat().clearBufferState();
            }

            if (polygonOffset != 0) {
                RenderSystem.polygonOffset(0f, 0f);
                RenderSystem.disablePolygonOffset();
            }
        }
    }

    public static class Pipeline {

        //        private final HashMap<ISkinPartType, Integer> partIndexes = new HashMap<>();
        private final HashMap<RenderType, ArrayList<Pass>> tasks = new HashMap<>();
        private int maxVertexCount = 0;

        public void add(Pass pass) {
            tasks.computeIfAbsent(pass.getRenderType(), k -> new ArrayList<>()).add(pass);
            maxVertexCount = Math.max(maxVertexCount, pass.getVertexCount());
//            pass.partIndex = partIndexes.computeIfAbsent(pass.getPartType(), partType -> partIndexes.size());
        }

        public void end() {
            if (tasks.isEmpty()) {
                return;
            }
            setupRenderState();

            int index = 0;
            for (SkinRenderType renderType : SkinRenderType.RENDER_ORDERING_FACES) {
                ArrayList<Pass> pendingPasses = tasks.get(renderType);
                if (pendingPasses == null || pendingPasses.isEmpty()) {
                    continue;
                }
                renderType.setupRenderState();
                for (Pass pass : pendingPasses) {
                    pass.render(renderType, index++, maxVertexCount);
                }
                renderType.clearRenderState();
            }

            clearRenderState();
            tasks.clear();
            maxVertexCount = 0;
        }

        private void setupRenderState() {
            RenderSystem.enableRescaleNormal();

            if (ModConfig.Client.enableWireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            }
        }

        private void clearRenderState() {
            if (ModConfig.Client.enableWireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }

            RenderSystem.disableRescaleNormal();
            SkinRenderObject.unbind();
        }
    }

    public static class Merger extends RenderType {

        protected Merger(String name, VertexFormat format, int mode, int bufferSize) {
            super(name, format, mode, bufferSize, true, false, Merger::noop, Merger::noop);
        }

        protected static void noop() {
        }

        @Override
        public void end(BufferBuilder builder, int p_228631_2_, int p_228631_3_, int p_228631_4_) {
            if (builder.building()) {
                builder.end();
            }
        }

        @Override
        public String toString() {
            return "RenderType[Merger[Skin]]";
        }
    }
}