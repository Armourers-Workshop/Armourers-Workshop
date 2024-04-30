package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeItemBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class ItemBuilderImpl<T extends Item> extends AbstractForgeItemBuilder<T> {

    public ItemBuilderImpl(Function<Item.Properties, T> supplier) {
        super(supplier);
    }

    @Override
    public IItemBuilder<T> stacksTo(int i) {
        this.properties = properties.stacksTo(i);
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
    public IItemBuilder<T> group(IRegistryKey<IItemGroup> group) {
        this.group = group;
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
}
