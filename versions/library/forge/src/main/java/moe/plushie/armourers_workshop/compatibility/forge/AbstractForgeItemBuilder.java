package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IItemStackRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryBinder;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemBuilder;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.ClientNativeManagerImpl;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.18, )")
public abstract class AbstractForgeItemBuilder<T extends Item> implements IItemBuilder<T> {

    protected Item.Properties properties = new Item.Properties();
    protected IRegistryBinder<T> binder;
    protected IRegistryKey<IItemGroup> group;
    protected final Function<Item.Properties, T> supplier;

    public AbstractForgeItemBuilder(Function<Item.Properties, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IItemBuilder<T> bind(Supplier<IItemStackRendererProvider> provider) {
        this.binder = () -> item -> {
            ClientNativeManagerImpl.INSTANCE.willRegisterItemRenderer(registry -> registry.register(item.get(), provider.get()));
        };
        return this;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        IRegistryKey<T> item = Registries.ITEM.register(name, () -> {
            T value = supplier.apply(properties);
            if (group != null) {
                group.get().add(() -> value);
            }
            return value;
        });
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, IRegistryBinder.of(binder, item));
        return item;
    }
}

