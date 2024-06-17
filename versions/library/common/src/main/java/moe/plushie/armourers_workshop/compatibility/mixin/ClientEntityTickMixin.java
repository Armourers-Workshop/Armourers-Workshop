package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientEntityTickMixin {

    @Inject(method = "tickNonPassenger", at = @At("RETURN"))
    private void aw2$tickNonPassenger(Entity entity, CallbackInfo ci) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            renderData.tick(entity);
        }
    }
}
