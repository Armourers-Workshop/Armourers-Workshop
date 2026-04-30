package moe.plushie.armourers_workshop.compat.fabric.mixin.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Available("[16, )")
@Mixin(LivingEntity.class)
public class FabricLivingEntityMixin {

    @Shadow
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<BlockPos> lastClimbablePos;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void aw2$isClimbing(CallbackInfoReturnable<Boolean> cir) {
        var entity = LivingEntity.class.cast(this);
        var level = entity.level();
        var blockPos = entity.blockPosition();
        var result = EntityLifecycleEvents.ALLOW_CLIMBING.invoker().allowClimbing(entity, blockPos, level.getBlockState(blockPos));
        if (result == OpenInteractionResult.SUCCESS) {
            lastClimbablePos = Optional.of(blockPos);
            cir.setReturnValue(true);
        }
        if (result == OpenInteractionResult.CONSUME) {
            cir.setReturnValue(false);
        }
    }
}
