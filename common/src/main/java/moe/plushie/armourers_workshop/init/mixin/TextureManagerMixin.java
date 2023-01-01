package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

    @Inject(method = "reload", at = @At("RETURN"), cancellable = true)
    public void aw$reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        DataPackManager.init(registry -> {
            CompletableFuture<Void> future = registry.reload(barrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            cir.setReturnValue(CompletableFuture.allOf(cir.getReturnValue(), future));
        });
    }
}
