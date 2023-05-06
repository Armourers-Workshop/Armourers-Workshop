package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.registry.IItemTagBuilder;
import net.minecraft.core.Registry;

public class ItemTagBuilderImpl<T extends IItemTag> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public IRegistryKey<T> build(String name) {
        return Registry.registerItemTagFA(name);
    }
}
