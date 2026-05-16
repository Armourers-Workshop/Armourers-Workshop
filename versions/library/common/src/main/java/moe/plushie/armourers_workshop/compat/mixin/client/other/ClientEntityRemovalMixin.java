package moe.plushie.armourers_workshop.compat.mixin.client.other;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[18, )")
@Mixin(Entity.class)
public class ClientEntityRemovalMixin {

    @Inject(method = "onClientRemoval", at = @At("HEAD"))
    private void aw2$onClientRemoval(CallbackInfo ci) {
        var entity = Entity.class.cast(this);
        var renderData = EntityRenderData.of(entity, false);
        if (renderData != null) {
            renderData.remove(entity);
        }
    }
}
