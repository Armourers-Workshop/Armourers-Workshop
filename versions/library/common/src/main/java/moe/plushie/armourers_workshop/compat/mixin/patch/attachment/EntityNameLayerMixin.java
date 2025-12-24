package moe.plushie.armourers_workshop.compat.mixin.patch.attachment;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientAttachmentHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, 1.26)")
@Mixin(EntityRenderer.class)
public class EntityNameLayerMixin<T extends Entity> {

    @Inject(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", shift = At.Shift.AFTER))
    private void aw$translateToName(T entity, Component component, PoseStack poseStackIn, MultiBufferSource multiBufferSourceIn, int i, float f, CallbackInfo ci) {
        ClientAttachmentHandler.onRenderName(entity, component, poseStackIn, multiBufferSourceIn);
    }
}
