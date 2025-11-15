package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;

public interface IBlockMenuProvider<C, T> {

    C createMenu(IMenuType<?> menuType, Block block, int containerId, Inventory inventory, T hostObject);
}
