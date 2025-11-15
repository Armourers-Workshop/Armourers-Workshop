package moe.plushie.armourers_workshop.compat.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITickable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Available("[1.18, )")
@Mixin(LevelChunk.class)
public abstract class TickableBlockEntityMixin {

    @ModifyVariable(method = "updateBlockEntityTicker", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;getTicker(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/BlockEntityType;)Lnet/minecraft/world/level/block/entity/BlockEntityTicker;", shift = At.Shift.AFTER))
    private <T extends BlockEntity> BlockEntityTicker<T> aw2$updateBlockEntityTicker(BlockEntityTicker<T> ticker, T blockEntity) {
        if (blockEntity instanceof ITickable tickable) {
            return (level, blockPos, blockState, blockEntity1) -> tickable.tick();
        }
        return ticker;
    }
}
