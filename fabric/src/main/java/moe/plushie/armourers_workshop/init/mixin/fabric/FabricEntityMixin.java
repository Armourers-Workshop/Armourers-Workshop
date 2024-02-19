package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.core.data.CapabilityStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class FabricEntityMixin implements CapabilityStorage.Provider {

    public CapabilityStorage aw2$capabilityStorage;

    @Override
    public CapabilityStorage getCapabilityStorage() {
        if (aw2$capabilityStorage == null) {
            aw2$capabilityStorage = CapabilityStorage.attachCapability(Entity.class.cast(this));
        }
        return aw2$capabilityStorage;
    }

    @Inject(method = "saveWithoutId", at = @At(value = "RETURN"))
    private void aw2$save(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
        getCapabilityStorage().save(Entity.class.cast(this), compoundTag);
    }

    @Inject(method = "load", at = @At(value = "RETURN"))
    private void aw2$load(CompoundTag compoundTag, CallbackInfo ci) {
        getCapabilityStorage().load(Entity.class.cast(this), compoundTag);
    }
}
