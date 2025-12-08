package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public class AbstractItemModels {

    private static final LateBoundIdMapper<OpenResourceLocation, IDataMapCodec<? extends AbstractItemModelImpl.Unbaked>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<AbstractItemModelImpl.Unbaked> CODEC = ID_MAPPER.codec(OpenResourceLocation.CODEC).dispatch(AbstractItemModelImpl.Unbaked::type, e -> e);

    public static void init() {
        // add builtin item model.
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("empty"), AbstractEmptyModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("model"), AbstractBlockModelWrapper.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("range_dispatch"), AbstractRangeSelectItemModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("special"), AbstractSpecialModelWrapper.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("composite"), AbstractCompositeModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("bundle/selected_item"), AbstractBundleSelectedItemSpecialRenderer.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("select"), AbstractSelectItemModel.Unbaked.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("condition"), AbstractConditionalItemModel.Unbaked.MAP_CODEC);
    }
}
