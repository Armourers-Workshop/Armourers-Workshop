package moe.plushie.armourers_workshop.compatibility.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

@Available("[1.20, )")
public abstract class AbstractResourceProviderImpl implements ResourceProvider {

    private final ResourceProvider impl;

    public AbstractResourceProviderImpl(ResourceProvider provider) {
        this.impl = provider;
    }

    public abstract Function<String, String> getTransformer(ResourceLocation rl);

    @Override
    public Optional<Resource> getResource(ResourceLocation location) {
        var results = impl.getResource(location);
        var transformer = getTransformer(location);
        if (transformer == null || results.isEmpty()) {
            return results;
        }
        var resource = results.get();
        return Optional.of(new Resource(resource.source(), () -> {
            var inputStream = resource.open();
            try {
                var source = StreamUtils.readStreamToString(inputStream, StandardCharsets.UTF_8);
                source = transformer.apply(source);
                return new ByteArrayInputStream(source.getBytes());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return inputStream;
        }, resource::metadata));
    }
}
