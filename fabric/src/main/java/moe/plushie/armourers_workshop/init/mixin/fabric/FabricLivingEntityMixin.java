package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityClimbingEvents;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class FabricLivingEntityMixin {

    @Shadow
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<BlockPos> lastClimbablePos;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void hooked_isClimbing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = ObjectUtils.unsafeCast(this);
        BlockPos blockPos = entity.blockPosition();
        InteractionResult result = EntityClimbingEvents.ALLOW_CLIMBING.invoker().allowClimbing(entity, blockPos, entity.getFeetBlockState());
        if (result == InteractionResult.SUCCESS) {
            lastClimbablePos = Optional.of(blockPos);
            cir.setReturnValue(true);
        }
        if (result == InteractionResult.CONSUME) {
            cir.setReturnValue(false);
        }
    }
}
