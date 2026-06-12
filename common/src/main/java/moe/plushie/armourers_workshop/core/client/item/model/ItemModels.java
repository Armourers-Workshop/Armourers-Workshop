package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class ItemModels {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends ItemModel.Unbaked>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<ItemModel.Unbaked> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(ItemModel.Unbaked::type, e -> e);

    public static void init() {
        // add builtin item model.
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("empty"), EmptyModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("model"), ItemModelWrapper.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("range_dispatch"), RangeSelectItemModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("special"), SpecialModelWrapper.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("composite"), CompositeModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("bundle/selected_item"), BundleSelectedItemSpecialRenderer.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("select"), SelectItemModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("condition"), ConditionalItemModel.Unbaked.MAP_CODEC);
    }
}
