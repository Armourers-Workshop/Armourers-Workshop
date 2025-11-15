package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

@SuppressWarnings("unused")
public interface IItemBuilder<T extends Item> extends IRegistryBuilder<T> {

    IItemBuilder<T> stacksTo(int i);

    IItemBuilder<T> durability(int i);

    IItemBuilder<T> craftRemainder(Item item);

    IItemBuilder<T> group(IRegistryHolder<CreativeModeTab> group);

    IItemBuilder<T> rarity(Rarity rarity);

    IItemBuilder<T> fireResistant();

    IItemBuilder<T> overrideDescription(String key);

    IItemBuilder<T> useDescriptionPrefix(String prefix);
}
