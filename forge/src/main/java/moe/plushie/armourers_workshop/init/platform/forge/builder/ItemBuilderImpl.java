package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IItemStackRendererProvider;
import moe.plushie.armourers_workshop.api.other.builder.IItemBuilder;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.core.registry.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilderImpl<T extends Item> implements IItemBuilder<T> {

    private Item.Properties properties = new Item.Properties();
    private Supplier<Consumer<T>> binder;
    private Function<Item.Properties, T> supplier;

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
        this.properties = properties.setISTER(() -> provider.get()::getItemModelRenderer);
        return this;
    }

    @Override
    public IRegistryObject<T> build(String name) {
        return Registry.ITEM.register(name, () -> supplier.apply(properties));
    }
}
