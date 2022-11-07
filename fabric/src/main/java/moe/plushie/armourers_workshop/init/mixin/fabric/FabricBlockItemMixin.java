package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.init.platform.fabric.event.PlayerBlockPlaceEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockItem.class)
public class FabricBlockItemMixin {

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void aw2$placeBlock(BlockPlaceContext blockPlaceContext, CallbackInfoReturnable<InteractionResult> cir, BlockPlaceContext blockPlaceContext2, BlockState blockState) {
        if (!PlayerBlockPlaceEvents.BEFORE.invoker().place(blockPlaceContext2, blockState)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
