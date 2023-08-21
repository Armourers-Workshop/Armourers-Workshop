package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, )")
@Mixin(RenderStateShard.class)
public class RenderStateMixin implements IRenderAttachable {

    private Runnable aw$attachment;

    @Override
    public void attachRenderTask(VertexConsumer buffer, Runnable runnable) {
        Runnable oldAttachment = aw$attachment;
        aw$attachment = runnable;
        if (oldAttachment != null) {
            return;
        }
        // we still need to add a placeholder block to the vertex buffer,
        // otherwise RenderType.clearRenderState maybe ignore of the empty vertexes.
        for (int i = 0; i < 4; ++i) {
            buffer.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    @Inject(method = "clearRenderState", at = @At("RETURN"))
    public void aw2$loadCallback(CallbackInfo ci) {
        if (aw$attachment != null) {
            aw$attachment.run();
            aw$attachment = null;
        }
    }
}
