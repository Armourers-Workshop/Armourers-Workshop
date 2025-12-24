package moe.plushie.armourers_workshop.compat.client.renderer.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceProviderImpl;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.List;
import java.util.function.Function;

@Available("[1.18, )")
public class AbstractShaderTransformer extends AbstractResourceProviderImpl {

    private final ShaderPreprocessor processor;
    private final List<ResourceLocation> selector;

    public AbstractShaderTransformer(ResourceProvider provider, ShaderPreprocessor processor) {
        this(provider, processor, null);
    }

    public AbstractShaderTransformer(ResourceProvider provider, ShaderPreprocessor processor, List<ResourceLocation> selector) {
        super(provider);
        this.processor = processor;
        this.selector = selector;
    }

    @Override
    public Function<String, String> getTransformer(ResourceLocation location) {
        // only process filter shader source.
        if (selector == null || selector.contains(location)) {
            return processor::process;
        }
        return null;
    }
}
