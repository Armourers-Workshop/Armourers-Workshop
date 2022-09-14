package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class AbstractEntityRenderDispatcherMixin {

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void hooked_loadCallback(ResourceManager resourceManager, CallbackInfo ci) {
        SkinRendererManager.getInstance().init();
    }
}
