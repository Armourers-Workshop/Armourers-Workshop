package moe.plushie.armourers_workshop.init.platform.forge.runtime;

import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientRegistryFactory;
import net.minecraft.core.Registry;

public class ClientRegistryPlatformImpl implements ClientRegistryFactory {

    @Override
    public TypedProvider<IKeyMapping> keyMapping() {
        return Registry.createKeyMappingRegistryFO();
    }

    @Override
    public TypedProvider<ItemTintSourceType<?>> itemTintSource() {
        return Registry.createItemTintSourceRegistryFO();
    }

    @Override
    public TypedProvider<BlockTintSourceType<?>> blockTintSource() {
        return Registry.createBlockTintSourceRegistryFO();
    }

    @Override
    public TypedProvider<SpecialModelRendererType<?>> specialModelRendererType() {
        return Registry.createSpecialModelRendererRegistryFO();
    }
}
