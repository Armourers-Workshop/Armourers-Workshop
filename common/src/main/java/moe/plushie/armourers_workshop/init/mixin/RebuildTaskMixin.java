package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.client.IBlockEntityExtendedRenderer;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class RebuildTaskMixin {

    @Inject(method = "handleBlockEntity", at = @At("HEAD"), cancellable = true)
    private void aw2$handleBlockEntity(ChunkRenderDispatcher.CompiledChunk compiledChunk, Set<BlockEntity> set, BlockEntity blockEntity, CallbackInfo ci) {
        IBlockEntityExtendedRenderer renderer = ObjectUtils.safeCast(blockEntity, IBlockEntityExtendedRenderer.class);
        if (renderer != null && !renderer.shouldUseExtendedRenderer()) {
            ci.cancel();
        }
    }
}
