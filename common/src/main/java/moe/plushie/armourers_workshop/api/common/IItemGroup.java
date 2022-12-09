package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface IItemGroup extends Supplier<CreativeModeTab> {

    void add(Supplier<Item> item);
}
