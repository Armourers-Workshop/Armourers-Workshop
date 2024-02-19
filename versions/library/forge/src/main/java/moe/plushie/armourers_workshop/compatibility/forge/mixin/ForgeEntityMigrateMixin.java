package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCapabilityManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
@Mixin(Entity.class)
public class ForgeEntityMigrateMixin {

    @Inject(method = "load", at = @At(value = "HEAD"))
    private void aw2$load(CompoundTag tag, CallbackInfo ci) {
        AbstractForgeCapabilityManager.migrate(ObjectUtils.unsafeCast(this), tag, tag, "adopt forge attachments");
    }
}
