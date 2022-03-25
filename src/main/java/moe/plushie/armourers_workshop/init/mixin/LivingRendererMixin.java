package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.init.client.ClientEventHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingRenderer.class)
public class LivingRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/EntityModel;setupAnim(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void hooked_render(T entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer buffers, int p_225623_6_, CallbackInfo ci) {
        ClientEventHandler.onRenderLiving((LivingRenderer<?, ?>) (Object) this, entity, p_225623_2_, p_225623_3_, matrixStack, buffers, p_225623_6_);
    }
}

