package riskyken.armourers_workshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourers_workshop.common.lib.LibBlockNames;

public class TileEntityDyeTable extends AbstractTileEntityInventory {
    
    private static final int INVENTORY_SIZE = 10;
    
    public TileEntityDyeTable() {
        super(INVENTORY_SIZE);
    }
    
    @Override
    public String getName() {
        return LibBlockNames.DYE_TABLE;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }
}
