package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Available("[18, )")
public class AbstractResourceProvider extends AbstractResourceProviderImpl {

    private final String type;

    public AbstractResourceProvider(ResourceProvider provider, String type) {
        super(provider);
        this.type = type;
    }

    public String type() {
        return type;
    }

    public Function<String, String> getTransformer(OpenResourceKey key) {
        return null;
    }

    @Override
    protected Function<InputStream, InputStream> createTransformer(OpenResourceKey key) {
        var transformer = getTransformer(key);
        if (transformer == null) {
            return null;
        }
        return inputStream -> {
            try {
                var source = StreamUtils.readStreamToString(inputStream, StandardCharsets.UTF_8);
                source = transformer.apply(source);
                return new ByteArrayInputStream(source.getBytes());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return inputStream;
        };
    }
}
