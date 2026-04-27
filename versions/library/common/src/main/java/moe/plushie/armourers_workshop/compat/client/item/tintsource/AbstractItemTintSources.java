package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class AbstractItemTintSources {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends IItemTintSource>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<IItemTintSource> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(IItemTintSource::type, e -> e);

    public static void init() {
        // add all custom item.
        ClientRegistries.ITEM_TINT_SOURCE_TYPES.forEach(it -> {
            // note that we evaluated it immediately.
            ID_MAPPER.put(it.registryName(), it.get().codec());
        });
        // add builtin item tint source.
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("custom_model_data"), AbstractCustomModelDataItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("constant"), AbstractConstantItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("dye"), AbstractDyeItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("grass"), AbstractGrassColorItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("firework"), AbstractFireworkItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("potion"), AbstractPotionItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("map_color"), AbstractMapColorItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("team"), AbstractTeamColorItemTintSource.MAP_CODEC);
    }
}
