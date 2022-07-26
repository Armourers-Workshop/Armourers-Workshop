package moe.plushie.armourers_workshop.api.other.builder;

import moe.plushie.armourers_workshop.api.common.IItemStackRendererProvider;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public interface IItemBuilder<T extends Item> extends IEntryBuilder<IRegistryObject<T>> {

    IItemBuilder<T> stacksTo(int i);

    IItemBuilder<T> defaultDurability(int i);

    IItemBuilder<T> durability(int i);

    IItemBuilder<T> craftRemainder(Item item);

    IItemBuilder<T> tab(CreativeModeTab creativeModeTab);

    IItemBuilder<T> rarity(Rarity rarity);

    IItemBuilder<T> fireResistant();

    IItemBuilder<T> bind(Supplier<IItemStackRendererProvider> provider);

}
