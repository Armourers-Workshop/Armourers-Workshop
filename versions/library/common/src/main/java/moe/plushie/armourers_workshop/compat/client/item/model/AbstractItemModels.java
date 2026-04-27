package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractItemModels {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends AbstractItemModelImpl.Unbaked>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<AbstractItemModelImpl.Unbaked> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(AbstractItemModelImpl.Unbaked::type, e -> e);

    public static void init() {
        // add builtin item model.
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("empty"), AbstractEmptyModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("model"), AbstractBlockModelWrapper.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("range_dispatch"), AbstractRangeSelectItemModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("special"), AbstractSpecialModelWrapper.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("composite"), AbstractCompositeModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("bundle/selected_item"), AbstractBundleSelectedItemSpecialRenderer.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("select"), AbstractSelectItemModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("condition"), AbstractConditionalItemModel.Unbaked.MAP_CODEC);
    }
}
