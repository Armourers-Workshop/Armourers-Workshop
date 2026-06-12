package moe.plushie.armourers_workshop.init.platform.fabric.runtime;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientRegistryFactory;
import net.minecraft.core.Registry;

public class ClientRegistryFactoryImpl implements ClientRegistryFactory {

    @Override
    public TypedProvider<IKeyMapping> keyMapping() {
        return Registry.createKeyMappingRegistryFA();
    }

    @Override
    public TypedProvider<ItemTintSourceType<?>> itemTintSource() {
        return Registry.createItemTintSourceRegistryFA();
    }

    @Override
    public TypedProvider<BlockTintSourceType<?>> blockTintSource() {
        return Registry.createBlockTintSourceRegistryFA();
    }

    @Override
    public TypedProvider<ISpecialModelRendererType<?>> specialModelRendererType() {
        return Registry.createSpecialModelRendererRegistryFA();
    }

}
