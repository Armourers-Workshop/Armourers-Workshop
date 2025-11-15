package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.player.Inventory;

@FunctionalInterface
public interface IMenuProvider<C, T> {

    C createMenu(IMenuType<?> menuType, int containerId, Inventory inventory, T value);
}
