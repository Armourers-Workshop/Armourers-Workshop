package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractItemBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

@SuppressWarnings("Convert2MethodRef")
public class ItemBuilderImpl<T extends Item> implements IItemBuilder<T> {

    private final AbstractItemBuilder<T> builder;

    public ItemBuilderImpl(Function<Item.Properties, T> factory) {
        this.builder = new AbstractItemBuilder<>(factory);
    }

    @Override
    public IItemBuilder<T> stacksTo(int i) {
        this.builder.apply(it -> it.stacksTo(i));
        return this;
    }

    @Override
    public IItemBuilder<T> durability(int i) {
        this.builder.apply(it -> it.durability(i));
        return this;
    }

    @Override
    public IItemBuilder<T> craftRemainder(Item item) {
        this.builder.apply(it -> it.craftRemainder(item));
        return this;
    }

    @Override
    public IItemBuilder<T> group(IRegistryHolder<CreativeModeTab> group) {
        this.builder.apply(group);
        return this;
    }

    @Override
    public IItemBuilder<T> rarity(Rarity rarity) {
        this.builder.apply(it -> it.rarity(rarity));
        return this;
    }

    @Override
    public IItemBuilder<T> fireResistant() {
        this.builder.apply(it -> it.fireResistant());
        return this;
    }

    @Override
    public IItemBuilder<T> overrideDescription(String key) {
        this.builder.apply(it -> it.overrideDescription(key));
        return this;
    }

    @Override
    public IItemBuilder<T> useDescriptionPrefix(String prefix) {
        this.builder.apply(it -> it.useDescriptionPrefix(prefix));
        return this;
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        return Registries.ITEMS.register(name, builder::build);
    }
}
