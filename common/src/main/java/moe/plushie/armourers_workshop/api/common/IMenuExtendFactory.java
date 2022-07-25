package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.player.Inventory;

@FunctionalInterface
public interface IMenuExtendFactory<C, T> {

    C createMenu(int containerId, Inventory inventory, T hostObject);
}
