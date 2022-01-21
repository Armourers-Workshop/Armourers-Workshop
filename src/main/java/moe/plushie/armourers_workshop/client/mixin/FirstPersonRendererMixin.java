package moe.plushie.armourers_workshop.client.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderer.class)
public class FirstPersonRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void hooked_renderItem(LivingEntity entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, boolean p_228397_4_, MatrixStack matrixStack, IRenderTypeBuffer renderType, int light, CallbackInfo callback) {
        if (itemStack.isEmpty()) {
            return;
        }
        ClientWardrobeHandler.onRenderItem(entity, itemStack, transformType, light, matrixStack, renderType, callback);
    }
}

