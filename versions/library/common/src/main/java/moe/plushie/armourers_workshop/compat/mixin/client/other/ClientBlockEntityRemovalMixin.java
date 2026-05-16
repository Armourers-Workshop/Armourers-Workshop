package moe.plushie.armourers_workshop.compat.mixin.client.other;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[16, )")
@Mixin(ClientLevel.class)
public class ClientBlockEntityRemovalMixin {

    @Inject(method = "unload", at = @At("HEAD"))
    private void aw2$onClientRemoval(LevelChunk chunk, CallbackInfo ci) {
        chunk.getBlockEntities().forEach((pos, blockEntity) -> {
            var renderData = BlockEntityRenderData.of(blockEntity, false);
            if (renderData != null) {
                renderData.remove(blockEntity);
            }
        });
    }
}
