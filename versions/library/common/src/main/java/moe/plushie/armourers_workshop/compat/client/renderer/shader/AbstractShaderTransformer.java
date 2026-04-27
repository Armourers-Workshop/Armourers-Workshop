package moe.plushie.armourers_workshop.compat.client.renderer.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceProvider;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.List;
import java.util.function.Function;

@Available("[18, )")
public class AbstractShaderTransformer extends AbstractResourceProvider {

    private final ShaderPreprocessor processor;
    private final List<OpenResourceKey> selector;

    public AbstractShaderTransformer(ResourceProvider provider, ShaderPreprocessor processor) {
        this(provider, processor, null);
    }

    public AbstractShaderTransformer(ResourceProvider provider, ShaderPreprocessor processor, List<OpenResourceKey> selector) {
        super(provider, "shader");
        this.processor = processor;
        this.selector = selector;
    }

    @Override
    public Function<String, String> getTransformer(OpenResourceKey key) {
        // only process filter shader source.
        if (selector == null || selector.contains(key)) {
            return processor::process;
        }
        return null;
    }
}
