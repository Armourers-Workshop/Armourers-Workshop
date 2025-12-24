package moe.plushie.armourers_workshop.compat.mixin.patch.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractClientHooks;
import moe.plushie.armourers_workshop.compat.client.renderer.shader.AbstractShaderSelector;
import moe.plushie.armourers_workshop.compat.client.renderer.shader.AbstractShaderTransformer;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceProvider;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Available("[1.18, 1.26)")
@Mixin(ShaderInstance.class)
public abstract class ShaderVanillaMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true)
    private static ResourceProvider aw2$createVanillaShader(ResourceProvider arg1, ResourceProvider arg2, String arg3, VertexFormat arg4) {
        AbstractClientHooks.createShaders();
        // this is a iris shader resource?
        if (arg1 instanceof AbstractResourceProvider provider) {
            return new AbstractShaderTransformer(arg1, new ShaderPreprocessor(provider.type(), 2));
        }
        // this is a vanilla shader resource.
        return new AbstractShaderTransformer(arg1, new ShaderPreprocessor("vanilla", 2), AbstractShaderSelector.DEFAULT);
    }
}
