package moe.plushie.armourers_workshop.compatibility.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.function.Function;

@Available("[1.18, )")
public class AbstractProgramProvider extends AbstractProgramProviderImpl {

    private final String type;
    private final ShaderPreprocessor preprocessor;

    public AbstractProgramProvider(String type, ShaderPreprocessor preprocessor, ResourceProvider provider) {
        super(provider);
        this.type = type;
        this.preprocessor = preprocessor;
    }

    @Override
    public Function<String, String> getTransformer(ResourceLocation location) {
        var path = location.getPath();
        if (path.endsWith("." + type)) {
            return preprocessor::process;
        }
        return null;
    }
}
