package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.utils.ShaderPreprocessor;
import moe.plushie.armourers_workshop.utils.ShaderUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "net.optifine.shaders.Shaders")
public class AbstractOptifineShaderMixin {

    @ModifyArg(method = "createVertShader", at = @At(value = "INVOKE", target = "Lnet/optifine/shaders/Shaders;shaderSource(ILjava/lang/String;)V"), remap = false)
    private static String aw$createVertShader(int shader, String value) {
        ShaderUniforms.clear();
        return new ShaderPreprocessor("va").process(value);
    }
}
