package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;

public class SkinRenderExecutor {

    private static Runnable pendingTask;

    public static void execute(IRenderType renderType, Runnable action) {
        pendingTask = () -> callout(action);
        callout(() -> {
            // we'll use vanilla's rendering system to immediately draw a transparent point,
            // and then we will get this call in `GlStateManager._drawElements`.
            var tesselator = AbstractBufferSource.tesselator();
            var buffer = tesselator.getBuffer(renderType);
            for (var i = 0; i < 4; ++i) {
                buffer.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
            tesselator.endBatch();
        });
        pendingTask = null;
    }

    public static void resume() {
        if (pendingTask == null) {
            return;
        }
        var action = pendingTask;
        pendingTask = null;
        action.run();
    }

    private static void callout(Runnable action) {
        action.run();
    }
}
