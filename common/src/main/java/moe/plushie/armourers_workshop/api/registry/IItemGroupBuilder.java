package moe.plushie.armourers_workshop.api.registry;

import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IItemGroupBuilder<T> extends IRegistryBuilder<T> {

    IItemGroupBuilder<T> icon(Supplier<Supplier<ItemStack>> icon);
}
