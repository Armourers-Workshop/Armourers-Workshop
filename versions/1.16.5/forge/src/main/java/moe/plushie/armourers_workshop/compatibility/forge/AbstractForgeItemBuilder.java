package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemBuilder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractItemStackRendererProvider;
import moe.plushie.armourers_workshop.core.registry.Registries;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractForgeItemBuilder<T extends Item> implements IItemBuilder<T> {

    protected Item.Properties properties = new Item.Properties();
    protected IRegistryKey<IItemGroup> group;
    protected final Function<Item.Properties, T> supplier;

    public AbstractForgeItemBuilder(Function<Item.Properties, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IItemBuilder<T> bind(Supplier<AbstractItemStackRendererProvider> provider) {
        this.properties = properties.setISTER(() -> () -> provider.get().create());
        return this;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        return Registries.ITEM.register(name, () -> {
            T value = supplier.apply(properties);
            if (group != null) {
                group.get().add(() -> value);
            }
            return value;
        });
    }
}

