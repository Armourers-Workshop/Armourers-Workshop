package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.customarmor.ArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public class TileEntityArmourerBrain extends TileEntity {

    private static final int MULTI_BLOCK_SIZE = 22;
    private static final long TICK_COOLDOWN = 40L;
    private static final long TICK_OFFSET = 5L;
    
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_TYPE = "type";
    private static final String TAG_FORMED = "formed";
    private static final String TAG_SKIRT_MODE = "skirtMode";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    
    private ForgeDirection direction;
    private ArmourerType type;
    private boolean formed;
    private boolean skirtMode;
    private int xOffset;
    private int zOffset;
    
    public TileEntityArmourerBrain() {
        this.direction = ForgeDirection.UNKNOWN;
        this.type = ArmourerType.LEGS;
        this.formed = false;
    }
    
    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) { return; }
        if (this.formed) { return; }
        if ((this.worldObj.getTotalWorldTime() + this.TICK_OFFSET) % TICK_COOLDOWN != 0L) { return; }
        checkForValidMultiBlock();
    }
    
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readFromNBT(compound);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public boolean checkForValidMultiBlock() {
        removeOldChildren();
        removeOldBoundingBoxed();
        
        if (!findMultiBlockCorner()) { return false; }
        
        for (int ix = 0; ix < MULTI_BLOCK_SIZE; ix++) {
            for (int iy = 0; iy < MULTI_BLOCK_SIZE; iy++) {
                if (ix == 0 | iy == 0 | ix == MULTI_BLOCK_SIZE - 1 | iy == MULTI_BLOCK_SIZE - 1) {
                    if (!validMultiBlockPart(xCoord + xOffset + ix, yCoord, zCoord + zOffset + iy)) {
                        return false;
                    }
                }
            }
        }
        
        for (int ix = 0; ix < MULTI_BLOCK_SIZE; ix++) {
            for (int iy = 0; iy < MULTI_BLOCK_SIZE; iy++) {
                if (ix == 0 | iy == 0 | ix == MULTI_BLOCK_SIZE - 1 | iy == MULTI_BLOCK_SIZE - 1) {
                    TileEntity te = worldObj.getTileEntity(xCoord + xOffset + ix, yCoord, zCoord + zOffset + iy);
                    if (te != null && te instanceof TileEntityMultiBlock) {
                        ((TileEntityMultiBlock)te).setParent(xCoord, yCoord, zCoord);
                    }
                }
            }
        } 
        
        ModLogger.log("valid " + xCoord + " " + yCoord + " " + zCoord);
        
        formed = true;
        makeBoundingBoxes();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }
    
    private void makeBoundingBoxes() {
        switch (type) {
        case HEAD:
            for (int ix = 0; ix < 8; ix++) {
                for (int iy = 0; iy < 8; iy++) {
                    for (int iz = 0; iz < 8; iz++) {
                        worldObj.setBlock(xCoord + xOffset + ix + 7, yCoord + iy, zCoord + zOffset + iz + 7, ModBlocks.boundingBox);
                    }
                }
            } 
            break;
        case CHEST:
            for (int ix = 0; ix < 8; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        worldObj.setBlock(xCoord + xOffset + ix + 7, yCoord + iy, zCoord + zOffset + iz + 4, ModBlocks.boundingBox);
                    }
                }
            } 
            break;
        default:
            break;
        }
    }
    
    private void removeOldChildren() {
        //clear old children
        for (int ix = 0; ix < MULTI_BLOCK_SIZE; ix++) {
            for (int iy = 0; iy < MULTI_BLOCK_SIZE; iy++) {
                if (ix == 0 | iy == 0 | ix == MULTI_BLOCK_SIZE - 1 | iy == MULTI_BLOCK_SIZE - 1) {
                    TileEntity te = worldObj.getTileEntity(xCoord + xOffset + ix, yCoord, zCoord + zOffset + iy);
                    if (te != null && te instanceof TileEntityMultiBlock) {
                        ((TileEntityMultiBlock)te).clearParent(xCoord, yCoord, zCoord);
                    }
                }
            }
        } 
    }
    
    private void removeOldBoundingBoxed() {
        //clear old bounding boxes
        for (int ix = 0; ix < MULTI_BLOCK_SIZE; ix++) {
            for (int iy = 0; iy < MULTI_BLOCK_SIZE; iy++) {
                for (int iz = 0; iz < MULTI_BLOCK_SIZE; iz++) {
                    if (worldObj.getBlock(xCoord + xOffset + ix, yCoord + iy, zCoord + zOffset + iz) == ModBlocks.boundingBox) {
                        worldObj.setBlockToAir(xCoord + xOffset + ix, yCoord + iy, zCoord + zOffset + iz);
                    }
                }
            }
        } 
    }
    
    public void preRemove() {
        removeOldChildren();
        removeOldBoundingBoxed();
    }
    
    private boolean validMultiBlockPart(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        TileEntity te;
        if (block == ModBlocks.armourerMultiBlock) {
            te = worldObj.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityMultiBlock) {
                if (!((TileEntityMultiBlock)te).getHasParent()) {
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
    
    private boolean findMultiBlockCorner() {
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
        
        return true;
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
    
    public void buildArmourItem(EntityPlayer player) {
        ArrayList<ArmourBlockData> armourBlockData = new ArrayList<ArmourBlockData>();
        
        for (int ix = 0; ix < MULTI_BLOCK_SIZE - 1; ix++) {
            for (int iy = 0; iy < MULTI_BLOCK_SIZE - 1; iy++) {
                for (int iz = 0; iz < MULTI_BLOCK_SIZE - 1; iz++) {
                    switch (type) {
                    case HEAD:
                        addArmourToList(xCoord + xOffset + ix, yCoord + iy, zCoord + zOffset + iz, ix, iy, iz, armourBlockData);
                        break;
                    case CHEST:
                        addArmourToList(xCoord + xOffset + ix, yCoord + iy, zCoord + zOffset + iz - 5, ix, iy, iz, armourBlockData);
                        break;
                    default:
                        break;
                    }
                    
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            ModLogger.log("setting armour data size " + armourBlockData.size() + " type " + type.name());
            CustomArmourData armourData = new CustomArmourData(armourBlockData, type);
            ClientProxy.AddCustomArmour(player, type, armourData);
        } else {
            ModLogger.log("removing armour data");
            ClientProxy.RemoveCustomArmour(player, type);
        }
    }
    
    private void addArmourToList(int x, int y, int z, int ix, int iy, int iz, ArrayList<ArmourBlockData> list) {
        if (worldObj.isAirBlock(x, y, z)) { return; }
        Block block = worldObj.getBlock(x, y, z);
        
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
            int colour = UtilBlocks.getColourFromTileEntity(worldObj ,x, y, z);
            ArmourBlockData blockData = new ArmourBlockData((ix - 11), 12 - (iy + 1), (iz - 11), colour, block == ModBlocks.colourableGlowing);
            list.add(blockData);
        }
    }
    
    public ArmourerType getType() {
        return type;
    }
    
    public boolean isFormed() {
        return formed;
    }
    
    public boolean isSkirtMode() {
        return skirtMode;
    }
    
    public int getXOffset() {
        return xOffset;
    }
    
    public int getZOffset() {
        return zOffset;
    }
    
    public ForgeDirection getDirection() {
        return direction;
    }
    
    public void setType(ArmourerType type) {
        this.type = type;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setDirection(ForgeDirection direction) {
        this.direction = direction;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setSkirtMode(boolean skirtMode) {
        this.skirtMode = skirtMode;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        direction = ForgeDirection.getOrientation(compound.getInteger(TAG_DIRECTION));
        type = ArmourerType.getOrdinal(compound.getInteger(TAG_TYPE));
        formed = compound.getBoolean(TAG_FORMED);
        skirtMode = compound.getBoolean(TAG_SKIRT_MODE);
        xOffset = compound.getInteger(TAG_X_OFFSET);
        zOffset = compound.getInteger(TAG_Z_OFFSET);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_DIRECTION, direction.ordinal());
        compound.setInteger(TAG_TYPE, type.ordinal());
        compound.setBoolean(TAG_FORMED, formed);
        compound.setBoolean(TAG_SKIRT_MODE, skirtMode);
        compound.setInteger(TAG_X_OFFSET, xOffset);
        compound.setInteger(TAG_Z_OFFSET, zOffset);
    }
}
