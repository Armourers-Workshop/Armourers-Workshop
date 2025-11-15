package moe.plushie.armourers_workshop.compat.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.List;
import java.util.function.Function;

@Available("[1.18, 1.22)")
public class AbstractShaderTransformer extends AbstractResourceProviderImpl {

    private static final List<ResourceLocation> SELECTORS = Collections.immutableList(builder -> {
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_entity_solid.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_entity_shadow.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_entity_cutout.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_energy_swirl.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_outline.vsh"));
    });

    private final ShaderPreprocessor preprocessor;

    public AbstractShaderTransformer(ResourceProvider provider, ShaderPreprocessor preprocessor) {
        super(provider);
        this.preprocessor = preprocessor;
    }

    @Override
    public Function<String, String> getTransformer(ResourceLocation location) {
        // only process filter shader source.
        if (SELECTORS.contains(location)) {
            return preprocessor::process;
        }
        return null;
    }
}
