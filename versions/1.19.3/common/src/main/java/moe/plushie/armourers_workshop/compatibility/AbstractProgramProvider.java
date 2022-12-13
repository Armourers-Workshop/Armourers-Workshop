package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class AbstractProgramProvider implements ResourceProvider {

    private final String type;
    private final ResourceProvider provider;
    private final ShaderPreprocessor preprocessor;

    public AbstractProgramProvider(String type, ShaderPreprocessor preprocessor, ResourceProvider provider) {
        this.type = type;
        this.provider = provider;
        this.preprocessor = preprocessor;
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation location) {
        Optional<Resource> results = provider.getResource(location);
        if (!location.getPath().endsWith("." + type) || results.isEmpty()) {
            return results;
        }
        Resource resource = results.get();
        return Optional.of(new Resource(resource.source(), () -> {
            InputStream inputStream = resource.open();
            try {
                String source = StreamUtils.toString(inputStream, StandardCharsets.UTF_8);
                source = preprocessor.process(source);
                return new ByteArrayInputStream(source.getBytes());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return inputStream;
        }, resource::metadata));
    }
}
