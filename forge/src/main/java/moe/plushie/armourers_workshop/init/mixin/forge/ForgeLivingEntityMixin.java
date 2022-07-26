package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.init.platform.forge.event.ClimbingLocationCheckEvent;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class ForgeLivingEntityMixin {

    @Shadow
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<BlockPos> lastClimbablePos;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void hooked_isClimbing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = ObjectUtils.unsafeCast(this);
        BlockPos blockPos = entity.blockPosition();
        ClimbingLocationCheckEvent event = new ClimbingLocationCheckEvent(entity, blockPos, entity.getFeetBlockState());
        MinecraftForge.EVENT_BUS.post(event);
        Event.Result result = event.getResult();
        if (result == Event.Result.ALLOW) {
            lastClimbablePos = Optional.of(blockPos);
            cir.setReturnValue(true);
        }
        if (result == Event.Result.DENY) {
            cir.setReturnValue(false);
        }
    }
}
