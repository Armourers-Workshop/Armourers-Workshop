package moe.plushie.armourers_workshop.compat.mixin.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.compat.client.AbstractClientHooks;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Available("[18, )")
@Conditional("optifine")
@Pseudo
@Mixin(targets = "net.optifine.shaders.Shaders")
public class ShaderOptifineMixin {

    @ModifyArg(method = "createVertShader", at = @At(value = "INVOKE", target = "Lnet/optifine/shaders/Shaders;shaderSource(ILjava/lang/String;)V"), remap = false)
    private static String aw2$createVertShader(int shader, String value) {
        AbstractClientHooks.createShaders();
        return new ShaderPreprocessor("optifine", 2).process(value);
    }
}
