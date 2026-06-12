package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.LateBoundIdMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.registry.ClientRegistries;

public class ItemTintSources {

    private static final LateBoundIdMapper<OpenResourceKey, IDataMapCodec<? extends ItemTintSource>> ID_MAPPER = new LateBoundIdMapper<>();
    public static final IDataCodec<ItemTintSource> CODEC = ID_MAPPER.codec(OpenResourceKey.CODEC).dispatch(ItemTintSource::type, e -> e);

    public static void init() {
        // add all custom item.
        ClientRegistries.ITEM_TINT_SOURCE_TYPES.forEach(it -> {
            // note that we evaluated it immediately.
            ID_MAPPER.put(it.registryName(), it.get().codec());
        });
        // add builtin item tint source.
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("custom_model_data"), CustomModelDataItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("constant"), ConstantItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("dye"), DyeItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("grass"), GrassColorItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("firework"), FireworkItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("potion"), PotionItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("map_color"), MapColorItemTintSource.MAP_CODEC);
        ID_MAPPER.put(OpenResourceKey.withDefaultNamespace("team"), TeamColorItemTintSource.MAP_CODEC);
    }
}
