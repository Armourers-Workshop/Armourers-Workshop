package moe.plushie.armourers_workshop.compat.mixin.core.other;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Available("[21, 26)")
@Mixin(EntityType.class)
public class VehicleEntityPlaceMixin {

    @Inject(method = "appendCustomEntityStackConfig", at = @At("RETURN"), cancellable = true)
    private static <T extends Entity> void aw2$appendCustomEntityStackConfig(Consumer<T> consumer, ServerLevel serverLevel, ItemStack itemStack, Player player, CallbackInfoReturnable<Consumer<T>> cir) {
        var oldResult = cir.getReturnValue();
        var newResult = SkinUtils.appendSkinIntoEntity(oldResult, serverLevel, itemStack, player);
        if (newResult != oldResult) {
            cir.setReturnValue(newResult);
        }
    }
}
