package moe.plushie.armourers_workshop.core.client.block.tintsource;

import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class BlockTintSources {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends BlockTintSource>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<BlockTintSource> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(BlockTintSource::type, e -> e);

    public static void init() {
        // add all custom item.
        ClientRegistries.BLOCK_TINT_SOURCE_TYPES.forEach(it -> {
            // note that we evaluated it immediately.
            ID_MAPPER.put(it.registryName(), it.get().codec());
        });
    }
}
