package moe.plushie.armourers_workshop.api.other.builder;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface IItemGroupBuilder<T> extends IEntryBuilder<T> {

    IItemGroupBuilder<T> icon(Supplier<Supplier<ItemStack>> icon);

    IItemGroupBuilder<T> appendItems(BiConsumer<List<ItemStack>, CreativeModeTab> appendItems);
}
