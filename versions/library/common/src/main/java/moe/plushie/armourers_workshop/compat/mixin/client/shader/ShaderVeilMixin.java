package moe.plushie.armourers_workshop.compat.mixin.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.compat.client.renderer.shader.AbstractShaderSelector;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceKey;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Available("[21, 26)")
@Conditional("veil")
@Pseudo
@Mixin(targets = "foundry.veil.impl.client.render.shader.processor.VanillaShaderProcessor")
public class ShaderVeilMixin {

    @ModifyVariable(method = "modify", at = @At("HEAD"), argsOnly = true, index = 6)
    private static String aw2$processSource(String source, Map<String, Object> customProgramData, @Nullable String shaderInstance, @Nullable ResourceLocation name) {
        // is a patched vanilla shader?
        var key = Objects.flatMap(name, AbstractResourceKey::wrap);
        if (AbstractShaderSelector.DEFAULT.contains(key)) {
            return new ShaderPreprocessor("vanilla", 2).process(source);
        }
        return source;
    }
}
