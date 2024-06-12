package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import net.minecraft.client.renderer.RenderType;

public class SkinRenderExecutor {

    private static Runnable pendingTask;

    public static void execute(RenderType renderType, Runnable action) {
        pendingTask = () -> callout(action);
        callout(() -> {
            // we'll use vanilla's rendering system to immediately draw a transparent point,
            // and then we will get this call in `GlStateManager._drawElements`.
            var buffers = AbstractBufferSource.tesselator();
            var buffer = buffers.getBuffer(renderType);
            for (var i = 0; i < 4; ++i) {
                buffer.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
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
        action.run();
    }
}
