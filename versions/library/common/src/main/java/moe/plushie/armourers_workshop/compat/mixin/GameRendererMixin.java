package moe.plushie.armourers_workshop.compat.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractClientHooks;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[20, 26)")
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "reloadShaders", at = @At("RETURN"))
    public void aw2$reloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        // all cached vertices buffer must reset.
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> AbstractClientHooks::reloadShaders);
    }
}
