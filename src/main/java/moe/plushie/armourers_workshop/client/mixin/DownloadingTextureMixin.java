package moe.plushie.armourers_workshop.client.mixin;

import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DownloadingTexture.class)
public class DownloadingTextureMixin {

    @Final
    @Shadow
    private String urlString;

    private static boolean slimModel = false;

    //
    @Inject(method = "processLegacySkin", at = @At(value = "HEAD"))
    private static void hooked_processLegacySkin(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
        slimModel = false;
        if (image != null && image.getWidth() > 54 && image.getHeight() > 20) {
            slimModel = (image.getPixelRGBA(54, 20) & 0xff000000) == 0;
        }
    }

    @Inject(method = "loadCallback", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getInstance()Lnet/minecraft/client/Minecraft;"))
    private void hooked_loadCallback(NativeImage image, CallbackInfo ci) {
        PlayerTextureLoader.getInstance().bakingTexture(urlString, image, slimModel);
    }
}