package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IItemStackRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemBuilder;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilderImpl<T extends Item> implements IItemBuilder<T> {

    private Item.Properties properties = new FabricItemSettings();
    private Supplier<Consumer<T>> binder;
    private final Function<Item.Properties, T> supplier;

    public ItemBuilderImpl(Function<Item.Properties, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IItemBuilder<T> stacksTo(int i) {
        this.properties = properties.stacksTo(i);
        return this;
    }

    @Override
    public IItemBuilder<T> defaultDurability(int i) {
        this.properties = properties.defaultDurability(i);
        return this;
    }

    @Override
    public IItemBuilder<T> durability(int i) {
        this.properties = properties.durability(i);
        return this;
    }

    @Override
    public IItemBuilder<T> craftRemainder(Item item) {
        this.properties = properties.craftRemainder(item);
        return this;
    }

    @Override
    public IItemBuilder<T> tab(CreativeModeTab creativeModeTab) {
        this.properties = properties.tab(creativeModeTab);
        return this;
    }

    @Override
    public IItemBuilder<T> rarity(Rarity rarity) {
        this.properties = properties.rarity(rarity);
        return this;
    }

    @Override
    public IItemBuilder<T> fireResistant() {
        this.properties = properties.fireResistant();
        return this;
    }

    @Override
    public IItemBuilder<T> bind(Supplier<IItemStackRendererProvider> provider) {
        this.binder = () -> item -> {
            // here is safe call client registry.
            BuiltinItemRendererRegistry.INSTANCE.register(item, provider.get().getItemModelRenderer()::renderByItem);
        };
        return this;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        IRegistryKey<T> object = Registry.ITEM.register(name, () -> supplier.apply(properties));
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, binder, object);
        return object;
    }
}
