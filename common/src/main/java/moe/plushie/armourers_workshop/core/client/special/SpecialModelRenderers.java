package moe.plushie.armourers_workshop.core.client.special;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class SpecialModelRenderers {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends SpecialModelRenderer<?>>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<SpecialModelRenderer<?>> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(it -> null, e -> e);

    public static void init() {
        // add all custom item.
        ClientRegistries.SPECIAL_MODEL_RENDERER_TYPES.forEach(it -> {
            // note that we evaluated it immediately.
            ID_MAPPER.put(it.registryName(), it.get().codec());
        });
    }
}
