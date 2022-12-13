package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.core.client.shader.ShaderPreprocessor;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
    public Resource getResource(ResourceLocation location) throws IOException {
        Resource resource = provider.getResource(location);
        if (!location.getPath().endsWith("." + type)) {
            return resource;
        }
        return new Resource() {
            @Override
            public ResourceLocation getLocation() {
                return resource.getLocation();
            }

            @Override
            public InputStream getInputStream() {
                InputStream inputStream = resource.getInputStream();
                try {
                    String source = StreamUtils.toString(inputStream, StandardCharsets.UTF_8);
                    source = preprocessor.process(source);
                    return new ByteArrayInputStream(source.getBytes());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return inputStream;
            }

            @Override
            public boolean hasMetadata() {
                return resource.hasMetadata();
            }

            @Nullable
            @Override
            public <T> T getMetadata(MetadataSectionSerializer<T> metadataSectionSerializer) {
                return resource.getMetadata(metadataSectionSerializer);
            }

            @Override
            public String getSourceName() {
                return resource.getSourceName();
            }

            @Override
            public void close() throws IOException {
                resource.close();
            }
        };
    }
}
