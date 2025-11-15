package moe.plushie.armourers_workshop.library.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;

public class CreativeSkinLibraryMenu extends SkinLibraryMenu {

    public CreativeSkinLibraryMenu(IMenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, playerInventory, access);
    }

    @Override
    public boolean shouldLoadStack() {
        return outputStack().isEmpty();
    }
}
