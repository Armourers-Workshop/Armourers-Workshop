package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

@Available("[19, 26)")
public abstract class AbstractResourceProviderImpl implements ResourceProvider {

    private final ResourceProvider impl;

    public AbstractResourceProviderImpl(ResourceProvider provider) {
        this.impl = provider;
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation location) {
        var resource = impl.getResource(location);
        var transformer = createTransformer(AbstractResourceKey.wrap(location));
        if (transformer == null || resource.isEmpty()) {
            return resource;
        }
        return Optional.of(AbstractResource.transform(resource.get(), transformer));
    }

    @Nullable
    protected abstract Function<InputStream, InputStream> createTransformer(OpenResourceKey key);
}
