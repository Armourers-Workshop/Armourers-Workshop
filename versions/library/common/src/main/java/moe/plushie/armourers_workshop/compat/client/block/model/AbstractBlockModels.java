package moe.plushie.armourers_workshop.compat.client.block.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractBlockModels {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends AbstractBlockModelImpl.Unbaked>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<AbstractBlockModelImpl.Unbaked> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(AbstractBlockModelImpl.Unbaked::type, e -> e);

    public static void init() {
        // add builtin item model.
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("model"), AbstractBlockModel.Unbaked.MAP_CODEC);
    }
}
