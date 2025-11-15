package moe.plushie.armourers_workshop.compat.client.renderer.special;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class AbstractSpecialModelRenderers {

    private static final LateBoundIdMapper<OpenResourceLocation, IDataMapCodec<? extends ISpecialModelRenderer<?>>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<ISpecialModelRenderer<?>> CODEC = ID_MAPPER.codec(OpenResourceLocation.CODEC).dispatch(it -> null, e -> e);

    public static void init() {
        // add all custom item.
        ClientRegistries.SPECIAL_MODEL_RENDERER_TYPES.forEach(it -> {
            // note that we evaluated it immediately.
            ID_MAPPER.put(it.registryName(), it.get().codec());
        });
    }
}
