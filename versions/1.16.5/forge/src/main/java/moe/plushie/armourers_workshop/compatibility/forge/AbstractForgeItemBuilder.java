package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IItemStackRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemBuilder;
import moe.plushie.armourers_workshop.core.registry.Registry;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractForgeItemBuilder<T extends Item> implements IItemBuilder<T> {

    protected Item.Properties properties = new Item.Properties();
    protected Supplier<Consumer<T>> binder;
    protected IRegistryKey<IItemGroup> group;
    protected final Function<Item.Properties, T> supplier;

    public AbstractForgeItemBuilder(Function<Item.Properties, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IItemBuilder<T> bind(Supplier<IItemStackRendererProvider> provider) {
        this.properties = properties.setISTER(() -> provider.get()::getItemModelRenderer);
        return this;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        return Registry.ITEM.register(name, () -> {
            T value = supplier.apply(properties);
            if (group != null) {
                group.get().add(() -> value);
            }
            return value;
        });
    }
}

