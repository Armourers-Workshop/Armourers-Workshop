package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL30;

public class SkinRenderExecutor {

    private static Runnable pendingTask;

    public static void execute(RenderType renderType, Runnable action) {
        pendingTask = () -> callout(action);
        callout(() -> {
            // we'll use vanilla's rendering system to immediately draw a transparent point,
            // and then we will get this call in `GlStateManager._drawElements`.
            MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            VertexConsumer buffer = buffers.getBuffer(renderType);
            for (int i = 0; i < 4; ++i) {
                buffer.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
            buffers.endBatch();
        });
        pendingTask = null;
    }

    public static void resume() {
        if (pendingTask == null) {
            return;
        }
        Runnable action = pendingTask;
        pendingTask = null;
        action.run();
    }

    private static void callout(Runnable action) {
        int vao = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int vbo = GL30.glGetInteger(GL30.GL_ARRAY_BUFFER_BINDING);
        int ibo = GL30.glGetInteger(GL30.GL_ELEMENT_ARRAY_BUFFER_BINDING);

        action.run();

        if (vao != 0) {
            GL30.glBindVertexArray(vao);
        }
        if (vbo != 0) {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        }
        if (ibo != 0) {
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo);
        }
    }
}
