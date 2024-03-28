package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityType.class)
public class VehicleEntityPlaceMixin {

    @Inject(method = "updateCustomEntityTag", at = @At("HEAD"))
    private static void aw2$updateCustomEntityTag(Level level, Player player, Entity entity, CompoundTag compoundTag, CallbackInfo ci) {
        SkinUtils.copyVehicleSkin(entity, compoundTag);
    }
}
