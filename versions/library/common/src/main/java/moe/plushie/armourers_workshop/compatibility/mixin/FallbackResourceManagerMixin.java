package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.texture.SmartResourceManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.19, )")
@Mixin(FallbackResourceManager.class)
public abstract class FallbackResourceManagerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void aw2$init(PackType packType, String id, CallbackInfo ci) {
        var resourceManager = SmartResourceManager.getInstance();
        if (resourceManager.getNamespaces(packType).contains(id)) {
            var resourceManager1 = FallbackResourceManager.class.cast(this);
            resourceManager1.push(resourceManager.getResources(packType));
        }
    }
}
