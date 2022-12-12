package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.compatibility.AbstractProgramProvider;
import moe.plushie.armourers_workshop.utils.ShaderPreprocessor;
import moe.plushie.armourers_workshop.utils.ShaderUniforms;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ShaderInstance.class)
public abstract class AbstractVanillaShaderMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true)
    private static ResourceProvider aw$createVanillaShader(ResourceProvider arg1, ResourceProvider arg2, String arg3, VertexFormat arg4) {
        ShaderUniforms.clear();
        // We just need to rewrite the used shader.
//        if (arg3.equals("rendertype_entity_solid") || arg3.equals("rendertype_entity_translucent") || arg3.equals("rendertype_solid") || arg3.equals("rendertype_translucent")) {
//            return new AbstractProgramProvider("vsh", new AbstractProgramPreprocessor(""), arg1);
//        }
        if (arg3.equals("rendertype_entity_solid") || arg3.equals("new_entity")) {
            return new AbstractProgramProvider("vsh", new ShaderPreprocessor(""), arg1);
        }
        return arg1;
    }
}
