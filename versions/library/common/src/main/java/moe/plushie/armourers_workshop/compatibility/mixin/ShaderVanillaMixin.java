package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.shader.AbstractProgramProvider;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.core.client.shader.ShaderUniforms;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Available("[1.18, )")
@Mixin(ShaderInstance.class)
public abstract class ShaderVanillaMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true)
    private static ResourceProvider aw$createVanillaShader(ResourceProvider arg1, ResourceProvider arg2, String arg3, VertexFormat arg4) {
        ShaderUniforms.clear();
        // We just need to rewrite the used shader.
        if (ShaderPreprocessor.PATCHED_VANILLA_SHADERS.contains(arg3)) {
            return new AbstractProgramProvider("vsh", new ShaderPreprocessor(""), arg1);
        }
        return arg1;
    }
}
