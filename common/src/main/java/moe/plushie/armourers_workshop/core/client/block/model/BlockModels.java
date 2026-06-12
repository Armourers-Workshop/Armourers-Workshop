package moe.plushie.armourers_workshop.core.client.block.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class BlockModels {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends BlockModel.Unbaked>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<BlockModel.Unbaked> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(BlockModel.Unbaked::type, e -> e);

    public static void init() {
        // add builtin item model.
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("model"), BlockModelWrapper.Unbaked.MAP_CODEC);
    }
}
