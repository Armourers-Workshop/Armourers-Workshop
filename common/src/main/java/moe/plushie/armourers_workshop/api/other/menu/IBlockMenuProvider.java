package moe.plushie.armourers_workshop.api.other.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

public interface IBlockMenuProvider<C, T> {

    C createMenu(MenuType<?> menuType, Block block, int containerId, Inventory inventory, T hostObject);
}
