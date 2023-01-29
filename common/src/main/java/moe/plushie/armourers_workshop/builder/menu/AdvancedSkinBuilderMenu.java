package moe.plushie.armourers_workshop.builder.menu;

import moe.plushie.armourers_workshop.api.common.IContainerLevelAccess;
import moe.plushie.armourers_workshop.core.menu.AbstractBlockContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AdvancedSkinBuilderMenu extends AbstractBlockContainerMenu {

    public AdvancedSkinBuilderMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IContainerLevelAccess access) {
        super(menuType, block, containerId, access);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }
}
