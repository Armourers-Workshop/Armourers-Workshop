package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.utils.ShaderPreprocessor;
import moe.plushie.armourers_workshop.compatibility.AbstractProgramProvider;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "net.coderbot.iris.pipeline.newshader.ExtendedShader")
public class AbstractIrisShaderMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true, remap = false)
    private static ResourceProvider aw$createIrisShader(ResourceProvider arg1, ResourceProvider arg2, String arg3, VertexFormat arg4) {
        return new AbstractProgramProvider("vsh", new ShaderPreprocessor("iris_"), arg1);
    }

}
