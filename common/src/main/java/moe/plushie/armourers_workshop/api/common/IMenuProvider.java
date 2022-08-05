package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

@FunctionalInterface
public interface IMenuProvider<C, T> {

    C createMenu(MenuType<?> menuType, int containerId, Inventory inventory, T hostObject);
}
