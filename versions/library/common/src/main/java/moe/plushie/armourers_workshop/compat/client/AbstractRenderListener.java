package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import net.minecraft.client.renderer.Sheets;

@Available("[1.16, )")
public class AbstractRenderListener {

    private static Runnable pendingTask;

    /**
     * A skin solid channel attach point.<br>
     * It will append to the solid block rendering after.
     */
    public static final IRenderType SKIN_ATTACHED_SOLID_SHEET = AbstractRenderType.of(Sheets.solidBlockSheet());

    /**
     * A skin translucent channel attach point.<br>
     * It will append to the translucent item rendering after.
     */
    public static final IRenderType SKIN_ATTACHED_TRANSLUCENT_SHEET = AbstractRenderType.of(Sheets.translucentItemSheet());

    /**
     * A skin outline channel attach point.<br>
     * It will append to the solid block outline rendering after.
     */
    public static final IRenderType SKIN_ATTACHED_OUTLINE_SHEET = SKIN_ATTACHED_SOLID_SHEET.outline().orElse(null);


    /**
     * Call back when render type is draw elements.
     */
    public static void drawElements(IRenderType renderType, IBufferSource bufferSource, Runnable callback) {
        pendingTask = () -> callout(callback);
        callout(() -> {
            // we'll use vanilla's rendering system to immediately draw a transparent point,
            // and then we will get this call in `GlStateManager._drawElements`.
            var buffer = bufferSource.getBuffer(renderType);
            for (var i = 0; i < 4; ++i) {
                buffer.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
            bufferSource.endBatch();
        });
        pendingTask = null;
    }

    /**
     * Call back when render type is end batch.
     */
    public static void endBatch(IRenderType renderType, IVertexConsumer builder, Runnable callback) {
        // build a face to tigger the required render type draw task.
        if (renderType.get() instanceof IRenderAttachable attachable) {
            attachable.attachRenderTask(builder, callback);
        }
    }

    public static void beginDrawElements() {

    }

    public static void endDrawElements() {
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
