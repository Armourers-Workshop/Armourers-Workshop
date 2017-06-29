package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityDyeTable extends AbstractTileEntityInventory {
    
    private static final int INVENTORY_SIZE = 10;
    
    public TileEntityDyeTable() {
        super(INVENTORY_SIZE);
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
