package moe.plushie.armourers_workshop.api.common.builder;

import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface IItemGroupBuilder<T> extends IEntryBuilder<IRegistryKey<T>> {

    IItemGroupBuilder<T> icon(Supplier<Supplier<ItemStack>> icon);
}
