package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.TridentRenderer;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentRenderer.class)
public class TridentRendererMixin<T extends TridentEntity> {

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void hooked_render(T entity, float p_225623_2_, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderType, int light, CallbackInfo callback) {
        ClientWardrobeHandler.onRenderTrident(entity, null, p_225623_2_, partialTicks, light, matrixStack, renderType, callback);
    }
}
