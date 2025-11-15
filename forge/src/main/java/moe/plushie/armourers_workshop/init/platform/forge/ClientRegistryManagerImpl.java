package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import moe.plushie.armourers_workshop.init.platform.ClientRegistryManager;
import net.minecraft.core.Registry;

@SuppressWarnings("unused")
public class ClientRegistryManagerImpl extends ClientRegistryManager {

    @Override
    public TypedProvider<IItemTintSourceType<?>> itemTintSources() {
        return Registry.createItemTintSourceRegistryFO();
    }

    @Override
    public TypedProvider<IBlockTintSourceType<?>> blockTintSources() {
        return Registry.createBlockTintSourceRegistryFO();
    }

    @Override
    public TypedProvider<ISpecialModelRendererType<?>> specialModelRendererTypes() {
        return Registry.createSpecialModelRendererRegistryFO();
    }
}
