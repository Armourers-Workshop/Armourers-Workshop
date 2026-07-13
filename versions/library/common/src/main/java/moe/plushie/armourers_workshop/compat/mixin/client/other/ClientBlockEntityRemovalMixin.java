package moe.plushie.armourers_workshop.compat.mixin.client.other;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[16, )")
@Mixin(BlockEntity.class)
public class ClientBlockEntityRemovalMixin {

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void aw2$onClientRemoval(CallbackInfo ci) {
        var blockEntity = BlockEntity.class.cast(this);
        if (blockEntity.isRemoved()) {
            return; // the block entity is already removed, ignore.
        }
        var renderData = BlockEntityRenderData.of(blockEntity, false);
        if (renderData == null) {
            return; // the render data not initialized yet, ignore.
        }
        renderData.remove(blockEntity);
    }
}
