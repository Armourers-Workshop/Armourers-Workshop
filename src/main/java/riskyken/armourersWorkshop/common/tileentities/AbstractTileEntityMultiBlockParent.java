package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;

public abstract class AbstractTileEntityMultiBlockParent extends AbstractTileEntityInventory {

    /** Size of the multi-blocks outer frame. */
    public static final int MULTI_BLOCK_SIZE = 22;
    
    /** How often the multi-block should check if it is valid. */
    private static final long TICK_COOLDOWN = 40L;
    
    /** Offset value for what tick to start on. */
    //TODO Dynamically change to a tick with less activity.
    private static final long TICK_OFFSET = 5L;
    
    /** Tags used for saving/loading NBT data. */
    private static final String TAG_FORMED = "formed";
    private static final String TAG_RECHECK = "recheck";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    
    /** Is the multi-block formed? */
    protected boolean formed;
    
    /** Should the multi-block be recheck on the next update? */
    private boolean recheck;
    
    /** Add to the xCoord to get the lowest x location on the multi-block.*/
    protected int xOffset;
    
    /** Add to the zCoord to get the lowest z location on the multi-block.*/
    protected int zOffset;
    
    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) {return; }
        if (this.formed & !this.recheck) { return; }
        if ((this.worldObj.getTotalWorldTime() + this.TICK_OFFSET) % TICK_COOLDOWN != 0L) { return; }
        checkForValidMultiBlock();
    }
    
    /**
     * Checks/rechecks the multi-block structure to see if it is valid.
     * Should only be called if the multi-block is not formed or needs a recheck.
     * @return True/false. Valid multi-block was formed.
     */
    public boolean checkForValidMultiBlock() {
        if (formed) {
            // If the multi-block is formed then we must be rechecking.
            removeOldChildren();
            multiBlockBroken();
            formed = false;
            recheck = false;
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        
        findMultiBlockCorner();
        
        //Check that we have a ring of valid multi-block parts.
        for (int ix = 0; ix < MULTI_BLOCK_SIZE; ix++) {
            for (int iy = 0; iy < MULTI_BLOCK_SIZE; iy++) {
                if (ix == 0 | iy == 0 | ix == MULTI_BLOCK_SIZE - 1 | iy == MULTI_BLOCK_SIZE - 1) {
                    if (!validMultiBlockPart(xCoord + xOffset + ix, yCoord, zCoord + zOffset + iy)) {
                        return false;
                    }
                }
            }
        }
        
        //Set each multi-block parts parent data to this block.
        for (int ix = 0; ix < MULTI_BLOCK_SIZE; ix++) {
            for (int iy = 0; iy < MULTI_BLOCK_SIZE; iy++) {
                if (ix == 0 | iy == 0 | ix == MULTI_BLOCK_SIZE - 1 | iy == MULTI_BLOCK_SIZE - 1) {
                    TileEntity te = worldObj.getTileEntity(xCoord + xOffset + ix, yCoord, zCoord + zOffset + iy);
                    if (te != null && te instanceof TileEntityMultiBlockChild) {
                        ((TileEntityMultiBlockChild)te).setParent(xCoord, yCoord, zCoord);
                    }
                }
            }
        } 
        
        formed = true;
        multiBlockFormed();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }
    
    /**
     * Clear the parent data from the multi-blocks child blocks.
     */
    private void removeOldChildren() {
        for (int ix = 0; ix < MULTI_BLOCK_SIZE; ix++) {
            //Why are we going up so high?
            for (int iy = 0; iy < MULTI_BLOCK_SIZE; iy++) {
                if (ix == 0 | iy == 0 | ix == MULTI_BLOCK_SIZE - 1 | iy == MULTI_BLOCK_SIZE - 1) {
                    TileEntity te = worldObj.getTileEntity(xCoord + xOffset + ix, yCoord, zCoord + zOffset + iy);
                    if (te != null && te instanceof TileEntityMultiBlockChild) {
                        ((TileEntityMultiBlockChild)te).clearParent(xCoord, yCoord, zCoord);
                    }
                }
            }
        } 
    }
    
    /**
     * Checks if the block at the given x, y and z is valid for a multi-block.
     * @param x X location.
     * @param y Y location.
     * @param z Z location.
     * @return Valid block true/false.
     */
    private boolean validMultiBlockPart(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        TileEntity te;
        if (block == ModBlocks.armourerMultiBlock) {
            te = worldObj.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityMultiBlockChild) {
                if (!((TileEntityMultiBlockChild)te).getHasParent()) {
                    return true;
                }
            }
        }
        if (block == ModBlocks.armourerBrain) {
            te = worldObj.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityArmourerBrain) {
                if (!((TileEntityArmourerBrain)te).isFormed()) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Finds the block with the lowest x and z value in the
     * multi-block and set the xOffset and zOffset using it.
     * @return 
     */
    private void findMultiBlockCorner() {
        ForgeDirection[] scanDirs =  { ForgeDirection.NORTH, ForgeDirection.WEST };
        
        if (formed) {
            formed = false;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        
        xOffset = 0;
        zOffset = 0;
        
        for (int i = 0; i < scanDirs.length; i++) {
            if (worldObj.getBlock(xOffset + xCoord + 1 * scanDirs[i].offsetX, yCoord, zOffset + zCoord + 1 * scanDirs[i].offsetZ) == ModBlocks.armourerMultiBlock) {
                for (int j = 0; j < MULTI_BLOCK_SIZE; j++) {
                    if (worldObj.getBlock(xOffset + xCoord + 1 * scanDirs[i].offsetX, yCoord, zOffset + zCoord + 1 * scanDirs[i].offsetZ)  == ModBlocks.armourerMultiBlock) {
                        if (i == 1) {
                            xOffset = -j;
                        } else {
                            zOffset = -j;
                        }
                    }
                }
                break;
            }
        }
        
        for (int i = scanDirs.length - 1; i >= 0; i--) {
            if (worldObj.getBlock(xOffset + xCoord + 1 * scanDirs[i].offsetX, yCoord, zOffset + zCoord + 1 * scanDirs[i].offsetZ) == ModBlocks.armourerMultiBlock) {
                for (int j = 0; j < MULTI_BLOCK_SIZE; j++) {
                    if (worldObj.getBlock(xOffset + xCoord + 1 * scanDirs[i].offsetX, yCoord, zOffset + zCoord + 1 * scanDirs[i].offsetZ)  == ModBlocks.armourerMultiBlock) {
                        if (i == 1) {
                            xOffset = -j;
                        } else {
                            zOffset = -j;
                        }
                    }
                }
                break;
            }
        }
    }
    
    /**
     * Called when the multi-block parent block is broken.
     */
    public void preRemove() {
        removeOldChildren();
        multiBlockBroken();
    }
    
    /**
     * Called when the multi-block is formed.
     */
    protected abstract void multiBlockFormed();
    
    /**
     * Called when the multi-block is broken.
     */
    protected abstract void multiBlockBroken();
    
    /**
     * Called whenever one of the child blocks is broken.
     */
    public void childUpdate() {
        recheck = true;
    }
    
    /**
     * Returns true if the multi-block formed.
     * @return Formed true/false.
     */
    public boolean isFormed() {
        return formed;
    }
    
    /**
     * Returns the X Offset of the multi-block.
     * @return X Offset.
     */
    public int getXOffset() {
        return xOffset;
    }
    
    /**
     * Returns the Z offset of the multi-block.
     * @return Z offset.
     */
    public int getZOffset() {
        return zOffset;
    }
    
    /**
     * Read server only data from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        recheck = compound.getBoolean(TAG_RECHECK);
    }
    
    /**
     * Write server only data from NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(TAG_RECHECK, recheck);
    }
    
    /**
     * Read data from NBT that has been shared with the client.
     */
    public void readCommonFromNBT(NBTTagCompound compound) {
        super.readCommonFromNBT(compound);
        formed = compound.getBoolean(TAG_FORMED);
        xOffset = compound.getInteger(TAG_X_OFFSET);
        zOffset = compound.getInteger(TAG_Z_OFFSET);
    }
    
    /**
     * Write data to NBT that needs to be shared with the client.
     */
    public void writeCommonToNBT(NBTTagCompound compound) {
        super.writeCommonToNBT(compound);
        compound.setBoolean(TAG_FORMED, formed);
        compound.setInteger(TAG_X_OFFSET, xOffset);
        compound.setInteger(TAG_Z_OFFSET, zOffset);
    }
}
