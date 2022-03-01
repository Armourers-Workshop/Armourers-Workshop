package moe.plushie.armourers_workshop.client.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowRenderer.class)
public class ArrowRendererMixin<T extends AbstractArrowEntity> {

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void hooked_render(T entity, float p_225623_2_, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderType, int light, CallbackInfo callback) {
        ClientWardrobeHandler.onRenderArrow(entity, null, p_225623_2_, partialTicks, light, matrixStack, renderType, callback);
    }
}
