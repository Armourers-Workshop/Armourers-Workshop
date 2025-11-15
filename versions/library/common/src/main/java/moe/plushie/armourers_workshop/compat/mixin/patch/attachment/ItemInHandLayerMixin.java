package moe.plushie.armourers_workshop.compat.mixin.patch.attachment;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractItemDisplayContext;
import moe.plushie.armourers_workshop.init.client.ClientAttachmentHandler;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Available("[1.20, 1.22)")
@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void aw$translateToHand(ItemInHandRenderer renderer, LivingEntity livingEntity, ItemStack itemStackIn, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int i) {
        ClientAttachmentHandler.onRenderHand(livingEntity, itemStackIn, AbstractItemDisplayContext.wrap(itemDisplayContext), poseStackIn, bufferSourceIn, itemStack -> {
            renderer.renderItem(livingEntity, itemStack, itemDisplayContext, bl, poseStackIn, bufferSourceIn, i);
        });
    }
}
