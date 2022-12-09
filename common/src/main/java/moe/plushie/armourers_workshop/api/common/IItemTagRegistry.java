package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface IItemTagRegistry<T extends Item> {

    Supplier<IItemTagKey<T>> register(String name);
}
