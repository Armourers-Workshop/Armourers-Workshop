package moe.plushie.armourers_workshop.client.mixin;


import moe.plushie.armourers_workshop.core.utils.DummyAtlasTexture;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ModelManager.class)
public class ModelManagerMixin {


    @Inject(method = "getAtlas", at = @At("HEAD"), cancellable = true)
    private void hooked_getAtlas(ResourceLocation location, CallbackInfoReturnable<AtlasTexture> cir) {
        if (location.equals(RenderUtils.TEX_ITEMS)) {
            cir.setReturnValue(DummyAtlasTexture.TEX_ITEMS);
        }
    }
}

