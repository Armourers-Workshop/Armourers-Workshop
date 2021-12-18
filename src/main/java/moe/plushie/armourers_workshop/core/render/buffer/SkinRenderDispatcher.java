package moe.plushie.armourers_workshop.core.render.buffer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderType;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderTypeBuffer;
import moe.plushie.armourers_workshop.core.render.buffer.SkinLightBuffer;
import moe.plushie.armourers_workshop.core.render.buffer.SkinVertexBuffer;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkinRenderDispatcher {

    private static final Map<Integer, SkinLightBuffer> SHARED_LIGHTS = new HashMap<>();

    private static int GROUP_COUNTER = 0;
    private static RenderGroup GROUP = null;

    public static void startBatch() {
        GROUP_COUNTER += 1;
        if (GROUP == null) {
            GROUP = new RenderGroup();
        }
    }

    public static void endBatch() {
        GROUP_COUNTER -= 1;
        if (GROUP_COUNTER != 0) {
            return;
        }
        flush();
    }

    public static void merge(SkinPart skinPart, int light, MatrixStack matrixStack, SkinRenderTypeBuffer buffer) {
        startBatch();
        buffer.forEach((renderType, vertexBuffer) -> GROUP.put(skinPart, light, matrixStack, renderType, vertexBuffer));
        endBatch();
    }

    public static void flush() {
        if (GROUP == null) {
            return;
        }
        GROUP.end();
        GROUP = null;
    }

    private static void setupRenderState() {
        RenderSystem.enableRescaleNormal();

        if (SkinConfig.enableWireframeRender) {
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
    }

    private static void clearRenderState() {
        if (SkinConfig.enableWireframeRender) {
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        RenderSystem.disableRescaleNormal();
        SkinVertexBuffer.unbind();
    }

    private static SkinLightBuffer getLightBuffer(int light) {
        return SHARED_LIGHTS.computeIfAbsent(light, SkinLightBuffer::new);
    }

    private static class RenderTask {
        int light;
        int polygonOffset;
        Matrix4f matrix4f;
        SkinVertexBuffer vertexBuffer;

        void render(int maxVertexCount, SkinRenderType renderType) {
            SkinLightBuffer lightBuffer = null;

            if (polygonOffset != 0) {
                RenderSystem.enablePolygonOffset();
                RenderSystem.polygonOffset(polygonOffset * -0.1f, 0.1f);
            }

            vertexBuffer.bind();
            vertexBuffer.getFormat().setupBufferState(0L);

            if (renderType.usesLight()) {
                lightBuffer = getLightBuffer(light);
                lightBuffer.ensureCapacity(maxVertexCount);
                lightBuffer.bind();
                lightBuffer.getFormat().setupBufferState(0L);
            }

            vertexBuffer.draw(matrix4f, renderType.mode());

            if (lightBuffer != null) {
                lightBuffer.getFormat().clearBufferState();
            }

            vertexBuffer.getFormat().clearBufferState();

            if (polygonOffset != 0) {
                RenderSystem.disablePolygonOffset();
            }
        }
    }

    private static class RenderGroup {

        private final Map<RenderType, ArrayList<RenderTask>> tasks = new HashMap<>();
        private int total = 0;
        private int maxVertexCount = 0;

        void put(SkinPart skinPart, int light, MatrixStack matrixStack, RenderType renderType, SkinVertexBuffer vertexBuffer) {
            int vertexCount = vertexBuffer.getVertexCount();
            if (vertexCount == 0) {
                return;
            }

            RenderTask t = new RenderTask();
            t.light = light;
            t.polygonOffset = total;// + skinPart.getType().getPolygonOffset();
            t.matrix4f = matrixStack.last().pose().copy();
            t.vertexBuffer = vertexBuffer;

            tasks.computeIfAbsent(renderType, (k) -> new ArrayList<>()).add(t);
            total += 1;
            if (vertexCount > maxVertexCount) {
                maxVertexCount = vertexCount;
            }
        }

        void end() {
            if (tasks.isEmpty()) {
                return;
            }
            setupRenderState();

            // Render all surfaces in defines order.
            for (SkinRenderType renderType : SkinRenderType.RENDER_ORDERING_FACES) {
                ArrayList<RenderTask> filteredTasks = tasks.get(renderType);
                if (filteredTasks == null || filteredTasks.isEmpty()) {
                    continue;
                }
                renderType.setupRenderState();
                for (RenderTask task : filteredTasks) {
                    task.render(maxVertexCount, renderType);
                }
                renderType.clearRenderState();
            }

            clearRenderState();
        }
    }
}
