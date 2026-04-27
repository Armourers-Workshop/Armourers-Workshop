package moe.plushie.armourers_workshop.compat.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Available("[21, )")
@Mixin(HopperBlockEntity.class)
public class FabricHopperBlockEntityMixin {

    @Inject(method = "getContainerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;DDD)Lnet/minecraft/world/Container;", at = @At("RETURN"), cancellable = true)
    private static void aw2$getContainerAt(Level level, BlockPos blockPos, BlockState blockState, double d, double e, double f, CallbackInfoReturnable<Container> cir) {
        var container = cir.getReturnValue();
        if (container instanceof SkinnableBlockEntity blockEntity && blockEntity.isLinked()) {
            // when a linked block entity, can't load self container.
            cir.setReturnValue(null);
        }
    }
}
