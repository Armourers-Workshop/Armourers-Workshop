package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.ShaderUniforms;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class AbstractGameRendererMixin {

    @Inject(method = "reloadShaders", at = @At("RETURN"))
    public void aw2$reloadShaders(ResourceManager resourceManager, CallbackInfo ci) {
        // all cached vertices buffer must reset.
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> () -> {
            ShaderUniforms.clear();
            SkinVertexBufferBuilder.clearAllCache();
        });
    }
}
