package moe.plushie.armourers_workshop.api.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface ICreativeModeTabBuilder<T extends CreativeModeTab> extends IRegistryBuilder<T> {

    ICreativeModeTabBuilder<T> icon(Supplier<Supplier<ItemStack>> icon);
}
