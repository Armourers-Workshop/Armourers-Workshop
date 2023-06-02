package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.client.shader.ShaderUniforms;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.18, 1.20)")
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "reloadShaders", at = @At("RETURN"))
    public void aw2$reloadShaders(ResourceManager resourceManager, CallbackInfo ci) {
        // all cached vertices buffer must reset.
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> () -> {
            ShaderUniforms.clear();
            SkinVertexBufferBuilder.clearAllCache();
        });
    }
}
