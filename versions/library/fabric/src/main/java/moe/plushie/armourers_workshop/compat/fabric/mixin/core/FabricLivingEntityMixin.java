package moe.plushie.armourers_workshop.compat.fabric.mixin.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.fabric.event.common.FabricEntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Available("[16, )")
@Mixin(LivingEntity.class)
public class FabricLivingEntityMixin {

    @Shadow
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<BlockPos> lastClimbablePos;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void aw2$isClimbing(CallbackInfoReturnable<Boolean> cir) {
        var entity = LivingEntity.class.cast(this);
        var blockPos = entity.blockPosition();
        var blockState = entity.level().getBlockState(blockPos);
        var result = new AtomicReference<OpenInteractionResult>(OpenInteractionResult.PASS);
        EventManager.post(FabricEntityEvent.StartClimbing.class, new FabricEntityEvent.StartClimbing() {
            @Override
            public LivingEntity entity() {
                return entity;
            }

            @Override
            public BlockPos blockPos() {
                return blockPos;
            }

            @Override
            public BlockState blockState() {
                return blockState;
            }

            @Override
            public void setResult(OpenInteractionResult value) {
                result.set(value);
            }
        });
        if (result.get() == OpenInteractionResult.SUCCESS) {
            lastClimbablePos = Optional.of(blockPos);
            cir.setReturnValue(true);
        }
        if (result.get() == OpenInteractionResult.CONSUME) {
            cir.setReturnValue(false);
        }
    }
}
