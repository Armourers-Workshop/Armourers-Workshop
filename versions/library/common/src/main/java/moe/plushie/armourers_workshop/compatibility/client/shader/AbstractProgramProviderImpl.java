package moe.plushie.armourers_workshop.compatibility.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

@Available("[1.20, )")
public abstract class AbstractProgramProviderImpl implements ResourceProvider {

    private final ResourceProvider impl;

    public AbstractProgramProviderImpl(ResourceProvider provider) {
        this.impl = provider;
    }

    public abstract Function<String, String> getTransformer(ResourceLocation rl);

    @Override
    public Optional<Resource> getResource(ResourceLocation location) {
        Optional<Resource> results = impl.getResource(location);
        Function<String, String> transformer = getTransformer(location);
        if (transformer == null || results.isEmpty()) {
            return results;
        }
        Resource resource = results.get();
        return Optional.of(new Resource(resource.source(), () -> {
            InputStream inputStream = resource.open();
            try {
                String source = StreamUtils.toString(inputStream, StandardCharsets.UTF_8);
                source = transformer.apply(source);
                return new ByteArrayInputStream(source.getBytes());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return inputStream;
        }, resource::metadata));
    }
}
