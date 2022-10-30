package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.compatibility.AbstractProgramPreprocessor;
import moe.plushie.armourers_workshop.compatibility.AbstractProgramProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ShaderInstance.class)
public abstract class AbstractVanillaShaderMixin implements ISkinDataProvider {

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), argsOnly = true)
    private static ResourceProvider aw$createVanillaShader(ResourceProvider arg1, ResourceProvider arg2, String arg3, VertexFormat arg4) {
        // We just need to rewrite the used shader.
//        if (arg3.equals("rendertype_entity_solid") || arg3.equals("rendertype_entity_translucent") || arg3.equals("rendertype_solid") || arg3.equals("rendertype_translucent")) {
//            return new AbstractProgramProvider("vsh", new AbstractProgramPreprocessor(""), arg1);
//        }
        if (arg3.equals("rendertype_entity_solid") || arg3.equals("new_entity")) {
            return new AbstractProgramProvider("vsh", new AbstractProgramPreprocessor(""), arg1);
        }
        return arg1;
    }

    public Object aw$skinData;

    @Override
    public <T> T getSkinData() {
        return ObjectUtils.unsafeCast(aw$skinData);
    }

    @Override
    public <T> void setSkinData(T data) {
        this.aw$skinData = data;
    }
}
