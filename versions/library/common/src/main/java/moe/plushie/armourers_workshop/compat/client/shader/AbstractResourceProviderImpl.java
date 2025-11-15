package moe.plushie.armourers_workshop.compat.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public Optional<Resource> getResource(ResourceLocation location) {
        var resource = impl.getResource(location);
        var transformer = getTransformer(location);
        if (transformer == null || resource.isEmpty()) {
            return resource;
        }
        var resource1 = resource.get();
        return Optional.of(new Resource(resource1.source(), () -> {
            var inputStream = resource1.open();
            try {
                var source = StreamUtils.readStreamToString(inputStream, StandardCharsets.UTF_8);
                source = transformer.apply(source);
                return new ByteArrayInputStream(source.getBytes());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return inputStream;
        }, resource1::metadata));
    }

    @Nullable
    public abstract Function<String, String> getTransformer(ResourceLocation location);
}
