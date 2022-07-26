package moe.plushie.armourers_workshop.api.other.menu;

import net.minecraft.world.entity.player.Inventory;

@FunctionalInterface
public interface IMenuProvider<C, T> {

    C createMenu(int containerId, Inventory inventory, T hostObject);
}
