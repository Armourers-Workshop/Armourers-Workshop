package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class AbstractItemTintSources {

    private static final LateBoundIdMapper<OpenResourceLocation, IDataMapCodec<? extends IItemTintSource>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<IItemTintSource> CODEC = ID_MAPPER.codec(OpenResourceLocation.CODEC).dispatch(IItemTintSource::type, e -> e);

    public static void init() {
        // add all custom item.
        ClientRegistries.ITEM_TINT_SOURCE_TYPES.forEach(it -> {
            // note that we evaluated it immediately.
            ID_MAPPER.put(it.registryName(), it.get().codec());
        });
        // add builtin item tint source.
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("custom_model_data"), AbstractCustomModelDataItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("constant"), AbstractConstantItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("dye"), AbstractDyeItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("grass"), AbstractGrassColorItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("firework"), AbstractFireworkItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("potion"), AbstractPotionItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("map_color"), AbstractMapColorItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceLocation.withDefaultNamespace("team"), AbstractTeamColorItemTintSource.MAP_CODEC);
    }
}
