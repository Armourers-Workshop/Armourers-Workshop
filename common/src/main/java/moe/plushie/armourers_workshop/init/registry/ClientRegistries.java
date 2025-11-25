package moe.plushie.armourers_workshop.init.registry;

import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.platform.ClientRegistryManager;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ClientRegistries {

    public static final TypedRegistry<IKeyMapping> KEY_BINDINGS = create("Key Mapping", ClientRegistryManager::keyMappings);

    public static final TypedRegistry<IItemTintSourceType<?>> ITEM_TINT_SOURCE_TYPES = create("Item Tint Source", ClientRegistryManager::itemTintSources);
    public static final TypedRegistry<IBlockTintSourceType<?>> BLOCK_TINT_SOURCE_TYPES = create("Block Tint Source", ClientRegistryManager::blockTintSources);

    public static final TypedRegistry<ISpecialModelRendererType<?>> SPECIAL_MODEL_RENDERER_TYPES = create("Special Model Renderer", ClientRegistryManager::specialModelRendererTypes);

    private static <T> TypedRegistry<T> create(String name, Function<ClientRegistryManager, TypedProvider<T>> provider) {
        return new TypedRegistry<>(name, provider.apply(ClientRegistryManager.getInstance()));
    }
}

