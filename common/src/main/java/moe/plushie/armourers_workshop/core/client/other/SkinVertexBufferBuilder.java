package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.AbstractShaderExecutor;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class SkinVertexBufferBuilder extends BufferBuilder implements MultiBufferSource {

    private static SkinVertexBufferBuilder MERGED_VERTEX_BUILDER;
    private static Matrix4f NORMAL_LIGHTMAP_MAT = Matrix4f.createScaleMatrix(1, 1, 1);

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
    }

    @Nonnull
    @Override
    public VertexConsumer getBuffer(@Nonnull RenderType renderType) {
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

    public abstract static class Pass {


        public abstract int getVertexOffset();

        public abstract int getVertexCount();

        public abstract int getLightmap();

        public abstract Matrix4f getModelViewMatrix();

        public abstract Matrix3f getInvNormalMatrix();

        public abstract ISkinPartType getPartType();

        public abstract RenderType getRenderType();

        public abstract VertexFormat getFormat();

        public abstract float getPolygonOffset();

        public abstract SkinRenderObject getVertexBuffer();

        public Matrix4f getLightmapTextureMatrix() {
            //#if MC >= 11800
            RenderType renderType = getRenderType();
            //#else
            //# RenderType renderType = SkinRenderType.FACE_LIGHTING;
            //#endif
            if (getRenderType() != SkinRenderType.FACE_SOLID && renderType != SkinRenderType.FACE_TRANSLUCENT) {
                return NORMAL_LIGHTMAP_MAT;
            }
            int lightmap = getLightmap();
            int u = lightmap & 0xffff;
            int v = (lightmap >> 16) & 0xffff;
            Matrix4f newValue = new Matrix4f();
            newValue.translate(new Vector3f(u, v, 0));
            return newValue;
        }

        public void render(RenderType renderType, int index, int maxVertexCount) {
            SkinRenderObject vertexBuffer = getVertexBuffer();
            AbstractShaderExecutor executor = AbstractShaderExecutor.getInstance();

            float polygonOffset = getPolygonOffset();
            if (polygonOffset != 0) {
                // https://sites.google.com/site/threejstuts/home/polygon_offset
                // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
                // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
                // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
                RenderSystem.polygonOffset(0, polygonOffset * -1);
                RenderSystem.enablePolygonOffset();
            }

            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderLight(getLightmap());

            RenderSystem.setExtendedLightmapTextureMatrix(getLightmapTextureMatrix());
            RenderSystem.setExtendedNormalMatrix(getInvNormalMatrix());
            RenderSystem.setExtendedModelViewMatrix(getModelViewMatrix());

            executor.setMaxVertexCount(maxVertexCount);
            executor.execute(vertexBuffer, getVertexOffset(), getVertexCount(), renderType, getFormat());

            if (polygonOffset != 0) {
                RenderSystem.polygonOffset(0f, 0f);
                RenderSystem.disablePolygonOffset();
            }
        }
    }

    public static class Pipeline {

        //        private final HashMap<ISkinPartType, Integer> partIndexes = new HashMap<>();
        private final AbstractShaderExecutor executor = AbstractShaderExecutor.getInstance();
        private final HashMap<RenderType, ArrayList<Pass>> tasks = new HashMap<>();

        private int maxVertexCount = 0;

        public void add(Pass pass) {
            tasks.computeIfAbsent(pass.getRenderType(), k -> new ArrayList<>()).add(pass);
            maxVertexCount = Math.max(maxVertexCount, pass.getVertexCount());
        }

        public void end() {
            if (tasks.isEmpty()) {
                return;
            }
            setupRenderState();

            int index = 0;
            for (RenderType renderType : SkinRenderType.RENDER_ORDERING_FACES) {
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
            RenderSystem.backupExtendedMatrix();
            RenderSystem.setExtendedTextureMatrix(Matrix4f.createTranslateMatrix(0, TickUtils.getPaintTextureOffset() / 256.0f, 0));
            executor.setup();
            if (ModDebugger.wireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            }
        }

        private void clearRenderState() {
            if (ModDebugger.wireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }
            executor.clean();
            SkinRenderObject.unbind();
            RenderSystem.restoreExtendedMatrix();
        }
    }
}
