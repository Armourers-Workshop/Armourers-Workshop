package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.AbstractRenderPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractShaderExecutor;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class SkinVertexBufferBuilder extends BufferBuilder implements MultiBufferSource {

    public static final RenderType MERGER = new Merger("skin_merger", DefaultVertexFormat.POSITION, 256);

    protected final Pipeline pipeline = new Pipeline();

    protected final HashMap<Skin, SkinRenderObjectBuilder> cachingBuilders = new HashMap<>();
    protected final HashMap<RenderType, BufferBuilder> cachingBuilders2 = new HashMap<>();

    protected final HashMap<Skin, SkinRenderObjectBuilder> pendingBuilders = new HashMap<>();

    protected final HashMap<RenderType, BufferBuilder> pendingBuilders2 = new HashMap<>();

    public SkinVertexBufferBuilder() {
        super(256);
    }

    public static SkinVertexBufferBuilder getBuffer(MultiBufferSource buffers) {
        VertexConsumer builder = buffers.getBuffer(MERGER);
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

    static final FloatBuffer buffer = FloatBuffer.allocate(9);

    public abstract static class Pass {

        public abstract int getLightmap();

        public abstract int getVertexOffset();

        public abstract int getVertexCount();

        public abstract Matrix4f getMatrix();

        public abstract Matrix3f getInvNormalMatrix();

        public abstract ISkinPartType getPartType();

        public abstract RenderType getRenderType();

        public abstract float getPolygonOffset();

        public abstract SkinRenderObject getVertexBuffer();

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
            RenderSystem.setTextureMatrix(Matrix4f.createTranslateMatrix(0, TickUtils.getPaintTextureOffset() / 256.0f, 0));
            RenderSystem.setInverseNormalMatrix(getInvNormalMatrix());

            AbstractRenderPoseStack modelViewStack = RenderSystem.getModelStack();
            modelViewStack.pushPose();
            modelViewStack.mulPose(getMatrix());
            modelViewStack.apply();

            executor.setMaxVertexCount(maxVertexCount);
            executor.execute(vertexBuffer, getVertexOffset(), getVertexCount(), renderType);

            modelViewStack.popPose();
            modelViewStack.apply();

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
            AbstractShaderExecutor.getInstance().setup();
            if (ModDebugger.wireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            }
        }

        private void clearRenderState() {
            if (ModDebugger.wireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }
            AbstractShaderExecutor.getInstance().clean();
            SkinRenderObject.unbind();
        }
    }

    public static class Merger extends RenderType {

        protected Merger(String name, VertexFormat format, int bufferSize) {
            super(name, format, SkinRenderType.FACE_SOLID.mode(), bufferSize, true, false, Merger::noop, Merger::noop);
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
