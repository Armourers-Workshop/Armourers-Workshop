package moe.plushie.armourers_workshop.common.tileentities;

import net.minecraft.tileentity.TileEntity;

public abstract class ModTileEntity extends TileEntity {
    
    /**
     * Sync the tile entity with the clients.
     */
    public void syncWithClients() {
        //worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    /**
     * Marks the tile entity as dirty and sync it with the clients.
     */
    public void dirtySync() {
        markDirty();
        syncWithClients();
    }
}
