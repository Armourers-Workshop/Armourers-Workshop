package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import net.minecraft.client.renderer.texture.HttpTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HttpTexture.class)
public class HttpTextureMixin {

    private static boolean slimModel = false;

    @Final
    @Shadow
    private String urlString;

    @Inject(method = "processLegacySkin", at = @At(value = "HEAD"))
    private static void hooked_processLegacySkin(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
        slimModel = false;
        if (image != null && image.getWidth() > 54 && image.getHeight() > 20) {
            slimModel = (image.getPixelRGBA(54, 20) & 0xff000000) == 0;
        }
    }

    @Inject(method = "load(Ljava/io/InputStream;)Lcom/mojang/blaze3d/platform/NativeImage;", at = @At(value = "RETURN"))
    private void hooked_loadCallback(CallbackInfoReturnable<NativeImage> ci) {
        PlayerTextureLoader.getInstance().receivePlayerTexture(urlString, ci.getReturnValue(), slimModel);
    }
}
