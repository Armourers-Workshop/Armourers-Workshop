package moe.plushie.armourers_workshop.compat.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Available("[1.16, 1.26)")
@Mixin(Entity.class)
public class VehicleEntityBreakMixin {

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "HEAD"))
    public void aw2$spawnAtLocation(ItemStack itemStack, float f, CallbackInfoReturnable<ItemEntity> cir) {
        var result = SkinUtils.appendSkinIntoItemStack(Objects::hash, Entity.class.cast(this));
        result.accept(itemStack);
    }
}
