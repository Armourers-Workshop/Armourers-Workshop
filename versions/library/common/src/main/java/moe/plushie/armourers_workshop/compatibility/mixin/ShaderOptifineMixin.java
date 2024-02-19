package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.core.client.shader.ShaderUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Available("[1.18, )")
@Pseudo
@Mixin(targets = "net.optifine.shaders.Shaders")
public class ShaderOptifineMixin {

    @ModifyArg(method = "createVertShader", at = @At(value = "INVOKE", target = "Lnet/optifine/shaders/Shaders;shaderSource(ILjava/lang/String;)V"), remap = false)
    private static String aw2$createVertShader(int shader, String value) {
        ShaderUniforms.clear();
        return new ShaderPreprocessor("va").process(value);
    }
}
