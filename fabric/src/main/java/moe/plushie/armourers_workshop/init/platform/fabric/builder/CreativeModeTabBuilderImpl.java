package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ICreativeModeTabBuilder;
import moe.plushie.armourers_workshop.compat.fabric.builder.AbstractFabricCreativeModeTabBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CreativeModeTabBuilderImpl<T extends CreativeModeTab> implements ICreativeModeTabBuilder<T> {

    private final AbstractFabricCreativeModeTabBuilder<T> builder;

    public CreativeModeTabBuilderImpl() {
        this.builder = new AbstractFabricCreativeModeTabBuilder<>();
    }

    @Override
    public ICreativeModeTabBuilder<T> icon(Supplier<Supplier<ItemStack>> icon) {
        this.builder.icon(icon);
        return this;
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        return Registries.CREATIVE_MODE_TABS.register(name, builder::build);
    }
}
