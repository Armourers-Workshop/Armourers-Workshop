package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.function.Function;

@Available("[1.18, )")
public class AbstractResourceProvider extends AbstractResourceProviderImpl {

    private final String type;

    public AbstractResourceProvider(ResourceProvider provider, String type) {
        super(provider);
        this.type = type;
    }

    @Override
    public Function<String, String> getTransformer(ResourceLocation location) {
        return null;
    }

    public String type() {
        return type;
    }
}
