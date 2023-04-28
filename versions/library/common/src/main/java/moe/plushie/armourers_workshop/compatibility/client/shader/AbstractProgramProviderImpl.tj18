package moe.plushie.armourers_workshop.compatibility.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
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
import java.util.function.Function;

@Available("[1.18, 1.19)")
public abstract class AbstractProgramProviderImpl implements ResourceProvider {

    private final ResourceProvider impl;

    public AbstractProgramProviderImpl(ResourceProvider provider) {
        this.impl = provider;
    }

    public abstract Function<String, String> getTransformer(ResourceLocation rl);

    @Override
    public Resource getResource(ResourceLocation location) throws IOException {
        Resource resource = impl.getResource(location);
        Function<String, String> transformer = getTransformer(location);
        if (transformer == null) {
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
                    source = transformer.apply(source);
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
