package moe.plushie.armourers_workshop.init.registry;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.platform.Platform;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientRegistryFactory;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ClientRegistries {

    public static final TypedRegistry<IKeyMapping> KEY_BINDINGS = create("Key Mapping", ClientRegistryFactory::keyMapping);

    public static final TypedRegistry<ItemTintSourceType<?>> ITEM_TINT_SOURCE_TYPES = create("Item Tint Source", ClientRegistryFactory::itemTintSource);
    public static final TypedRegistry<BlockTintSourceType<?>> BLOCK_TINT_SOURCE_TYPES = create("Block Tint Source", ClientRegistryFactory::blockTintSource);

    public static final TypedRegistry<ISpecialModelRendererType<?>> SPECIAL_MODEL_RENDERER_TYPES = create("Special Model Renderer", ClientRegistryFactory::specialModelRendererType);

    private static <T> TypedRegistry<T> create(String name, Function<ClientRegistryFactory, TypedProvider<T>> provider) {
        return new TypedRegistry<>(name, provider.apply(Platform.get().client().registry()));
    }
}

