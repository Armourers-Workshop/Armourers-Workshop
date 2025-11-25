
package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

public abstract class ClientRegistryManager {

    private static final ClientRegistryManager INSTANCE = PlatformLoader.load(ClientRegistryManager.class);

    public static ClientRegistryManager getInstance() {
        return INSTANCE;
    }

    public abstract TypedProvider<IKeyMapping> keyMappings();

    public abstract TypedProvider<IItemTintSourceType<?>> itemTintSources();

    public abstract TypedProvider<IBlockTintSourceType<?>> blockTintSources();

    public abstract TypedProvider<ISpecialModelRendererType<?>> specialModelRendererTypes();
}
