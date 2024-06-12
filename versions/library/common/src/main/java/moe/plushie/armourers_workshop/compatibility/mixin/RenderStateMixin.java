package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, )")
@Mixin(RenderStateShard.class)
public class RenderStateMixin implements IRenderAttachable {

    private Runnable aw2$attachment;

    @Override
    public void attachRenderTask(IVertexConsumer buffer, Runnable runnable) {
        var oldAttachment = aw2$attachment;
        aw2$attachment = runnable;
        if (oldAttachment != null) {
            return;
        }
        // we still need to add a placeholder block to the vertex buffer,
        // otherwise RenderType.clearRenderState maybe ignore of the empty vertexes.
        for (int i = 0; i < 4; ++i) {
            buffer.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    @Inject(method = "clearRenderState", at = @At("RETURN"))
    public void aw2$loadCallback(CallbackInfo ci) {
        if (aw2$attachment != null) {
            aw2$attachment.run();
            aw2$attachment = null;
        }
    }
}
