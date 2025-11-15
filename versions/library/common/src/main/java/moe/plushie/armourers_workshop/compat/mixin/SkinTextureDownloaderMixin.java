package moe.plushie.armourers_workshop.compat.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractNativeImage;
import moe.plushie.armourers_workshop.compat.client.AbstractRemoteTextureData;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Available("[1.18, 1.22)")
@Mixin(HttpTexture.class)
public class SkinTextureDownloaderMixin {

    private AbstractRemoteTextureData aw2$textureData;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void aw2$init(File file, String url, ResourceLocation placeholder, boolean bl, Runnable runnable, CallbackInfo ci) {
        this.aw2$textureData = AbstractRemoteTextureData.of(url);
    }

    @Inject(method = "processLegacySkin", at = @At(value = "HEAD"))
    private void aw2$processLegacySkin(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
        var slimModel = false;
        if (image != null && image.getWidth() > 54 && image.getHeight() > 20) {
            slimModel = (image.getPixelRGBA(54, 20) & 0xff000000) == 0;
        }
        aw2$textureData.setSlimModel(slimModel);
    }

    @Inject(method = "load(Ljava/io/InputStream;)Lcom/mojang/blaze3d/platform/NativeImage;", at = @At(value = "RETURN"))
    private void aw2$loadCallback(CallbackInfoReturnable<NativeImage> ci) {
        aw2$textureData.setData(AbstractNativeImage.of(ci.getReturnValue()).clone());
    }
}
