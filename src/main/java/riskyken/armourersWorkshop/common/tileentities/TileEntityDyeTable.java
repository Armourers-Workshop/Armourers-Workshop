package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityDyeTable extends AbstractTileEntityInventory {

    public TileEntityDyeTable() {
        items = new ItemStack[9];
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }
    
    @Override
    public String getInventoryName() {
        return LibBlockNames.DYE_TABLE;
    }
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }
}
