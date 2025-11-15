package moe.plushie.armourers_workshop.compat.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.shader.AbstractResourceProvider;
import moe.plushie.armourers_workshop.compat.client.shader.AbstractShaderTransformer;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.core.client.shader.ShaderUniforms;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Available("[1.18, 1.22)")
@Mixin(ShaderInstance.class)
public abstract class ShaderVanillaMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true)
    private static ResourceProvider aw2$createVanillaShader(ResourceProvider arg1, ResourceProvider arg2, String arg3, VertexFormat arg4) {
        ShaderUniforms.clear();
        // this is a iris shader resource?
        if (arg1 instanceof AbstractResourceProvider provider) {
            return new AbstractShaderTransformer(arg1, new ShaderPreprocessor(provider.type(), 1));
        }
        // this is a vanilla shader resource.
        return new AbstractShaderTransformer(arg1, new ShaderPreprocessor("vanilla", 1));
    }
}
