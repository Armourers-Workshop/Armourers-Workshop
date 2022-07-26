package moe.plushie.armourers_workshop.library.menu;

import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class CreativeSkinLibraryMenu extends SkinLibraryMenu {

    public CreativeSkinLibraryMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(containerId, ModMenus.SKIN_LIBRARY_CREATIVE, ModBlocks.SKIN_LIBRARY_CREATIVE, playerInventory, access);
    }

    @Override
    public boolean shouldLoadStack() {
        return getOutputStack().isEmpty();
    }
}