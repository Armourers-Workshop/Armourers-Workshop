package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityArmourLibrary extends AbstractTileEntityInventory {

    public TileEntityArmourLibrary() {
        this.items = new ItemStack[2];
    }
    
    @Override
    public String getInventoryName() {
        return LibBlockNames.ARMOUR_LIBRARY;
    }
}
