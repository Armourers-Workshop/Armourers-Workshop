package moe.plushie.armourers_workshop.compat.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.data.CapabilityStorage;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Available("[16, 26)")
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
    private void aw2$save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        var entity = Entity.class.cast(this);
        getCapabilityStorage().save(entity, new TagSerializer(tag, SerializationContext.from(entity)));
    }

    @Inject(method = "load", at = @At(value = "RETURN"))
    private void aw2$load(CompoundTag tag, CallbackInfo ci) {
        var entity = Entity.class.cast(this);
        getCapabilityStorage().load(entity, new TagSerializer(tag, SerializationContext.from(entity)));
    }
}
