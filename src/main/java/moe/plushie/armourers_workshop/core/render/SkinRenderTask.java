package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkinRenderTask {

    private final int vertexOffset;
    private final int vertexCount;
    private final Parameter parameter;
    private final RenderType renderType;
    private final SkinVertexBuffer vertexBuffer;
    private int id;

    public SkinRenderTask(RenderType renderType, SkinVertexBuffer vertexBuffer, int vertexOffset, int vertexCount, Parameter parameter) {
        this.renderType = renderType;
        this.vertexBuffer = vertexBuffer;
        this.vertexCount = vertexCount;
        this.vertexOffset = vertexOffset;
        this.parameter = parameter;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public void render(SkinRenderType renderType, int maxVertexCount) {
        SkinLightBuffer lightBuffer = null;

        if (id != 0) {
            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(id * -0.01f, -3f);
        }

        if (renderType.usesLight()) {
            lightBuffer = SkinLightBuffer.getLightBuffer(parameter.light);
            lightBuffer.ensureCapacity(maxVertexCount);
            lightBuffer.bind();
            lightBuffer.getFormat().setupBufferState(0L);
        }

        vertexBuffer.bind();
        renderType.format().setupBufferState(vertexOffset);

        vertexBuffer.draw(parameter.matrix, renderType.mode(), vertexCount);

        renderType.format().clearBufferState();

        if (lightBuffer != null) {
            lightBuffer.getFormat().clearBufferState();
        }

        if (id != 0) {
            RenderSystem.disablePolygonOffset();
            RenderSystem.polygonOffset(0f, 0f);
        }
    }


    public static class Parameter {
        Matrix4f matrix;
        int light;
        int partialTicks;

        public Parameter(MatrixStack matrixStack, int light, int partialTicks) {
            this.matrix = matrixStack.last().pose().copy();
            this.light = light;
            this.partialTicks = partialTicks;
        }

    }

    public static class Group {

        private final Map<RenderType, ArrayList<SkinRenderTask>> tasks = new HashMap<>();
        private int total = 0;
        private int maxVertexCount = 0;

        public void add(SkinRenderTask task) {
            task.id = total++;
            tasks.computeIfAbsent(task.renderType, (k) -> new ArrayList<>()).add(task);
            maxVertexCount = Math.max(maxVertexCount, task.vertexCount);
        }

        public void endBatch() {
            if (tasks.isEmpty()) {
                return;
            }
            setupRenderState();
            for (SkinRenderType renderType : SkinRenderType.RENDER_ORDERING_FACES) {
                ArrayList<SkinRenderTask> pendingTasks = tasks.get(renderType);
                if (pendingTasks == null || pendingTasks.isEmpty()) {
                    continue;
                }
                renderType.setupRenderState();
                for (SkinRenderTask task : pendingTasks) {
                    task.render(renderType, maxVertexCount);
                }
                renderType.clearRenderState();
            }
            clearRenderState();
        }

        private void setupRenderState() {
            RenderSystem.enableRescaleNormal();

            if (SkinConfig.enableWireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            }
        }

        private void clearRenderState() {
            if (SkinConfig.enableWireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }

            RenderSystem.disableRescaleNormal();
            SkinVertexBuffer.unbind();
        }
    }
}
