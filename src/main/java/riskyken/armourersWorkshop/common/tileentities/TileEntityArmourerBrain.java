package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.customarmor.ArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.ArmourPart;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourData;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourManager;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public class TileEntityArmourerBrain extends AbstractTileEntityInventory {

    public static final int MULTI_BLOCK_SIZE = 22;
    private static final long TICK_COOLDOWN = 40L;
    private static final long TICK_OFFSET = 5L;
    
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_TYPE = "type";
    private static final String TAG_FORMED = "formed";
    private static final String TAG_SKIRT_MODE = "skirtMode";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    
    private ForgeDirection direction;
    private ArmourerType type;
    private boolean formed;
    private boolean skirtMode;
    private int xOffset;
    private int zOffset;
    private boolean showGuides;
    
    public TileEntityArmourerBrain() {
        this.direction = ForgeDirection.UNKNOWN;
        this.type = ArmourerType.LEGS;
        this.formed = false;
        this.items = new ItemStack[2];
    }
    
    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) { return; }
        if (this.formed) { return; }
        if ((this.worldObj.getTotalWorldTime() + this.TICK_OFFSET) % TICK_COOLDOWN != 0L) { return; }
        checkForValidMultiBlock();
    }
    
    public boolean checkForValidMultiBlock() {
        removeOldChildren();
        removeBoundingBoxed();
        
        findMultiBlockCorner();
        
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
        createBoundingBoxes();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }
    
    private void createBoundingBoxes() {
        switch (type) {
        case HEAD:
            for (int ix = 0; ix < 8; ix++) {
                for (int iy = 0; iy < 8; iy++) {
                    for (int iz = 0; iz < 8; iz++) {
                        worldObj.setBlock(xCoord + xOffset + ix + 7, yCoord + iy + 2, zCoord + zOffset + iz + 7, ModBlocks.boundingBox);
                    }
                }
            } 
            break;
        case CHEST:
            //Chest
            for (int ix = 0; ix < 8; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        worldObj.setBlock(xCoord + xOffset + ix + 7, yCoord + iy + 2, zCoord + zOffset + iz + 4, ModBlocks.boundingBox);
                    }
                }
            } 
            for (int ix = 0; ix < 4; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        //Right Arm
                        worldObj.setBlock(xCoord + xOffset + ix + 4, yCoord + iy + 2, zCoord + zOffset + iz + 14, ModBlocks.boundingBox);
                        //Left Arm
                        worldObj.setBlock(xCoord + xOffset + ix + 14, yCoord + iy + 2, zCoord + zOffset + iz + 14, ModBlocks.boundingBox);
                    }
                }
            }
            break;
            
        case LEGS:
            for (int ix = 0; ix < 4; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        if (!isSkirtMode()) {
                            //Right Leg
                            worldObj.setBlock(xCoord + xOffset + ix + 4, yCoord + iy + 2, zCoord + zOffset + iz + 9, ModBlocks.boundingBox);
                            //Left Leg
                            worldObj.setBlock(xCoord + xOffset + ix + 14, yCoord + iy + 2, zCoord + zOffset + iz + 9, ModBlocks.boundingBox);
                        } else {
                            //Right Leg
                            worldObj.setBlock(xCoord + xOffset + ix + 7, yCoord + iy + 2, zCoord + zOffset + iz + 9, ModBlocks.boundingBox);
                            //Left Leg
                            worldObj.setBlock(xCoord + xOffset + ix + 11, yCoord + iy + 2, zCoord + zOffset + iz + 9, ModBlocks.boundingBox);
                        }
                    }
                }
            }
            break;
        case FEET:
            for (int ix = 0; ix < 4; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        //Right Leg
                        worldObj.setBlock(xCoord + xOffset + ix + 4, yCoord + iy + 2, zCoord + zOffset + iz + 9, ModBlocks.boundingBox);
                        //Left Leg
                        worldObj.setBlock(xCoord + xOffset + ix + 14, yCoord + iy + 2, zCoord + zOffset + iz + 9, ModBlocks.boundingBox);
                    }
                }
            }
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
    
    private void removeBoundingBoxed() {
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
        removeBoundingBoxed();
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
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
    
    public void buildArmourItem(EntityPlayer player) {
        switch (type) {
        case HEAD:
            buildArmourPart(player, ArmourPart.HEAD);
            break;
        case CHEST:
            buildArmourPart(player, ArmourPart.CHEST);
            buildArmourPart(player, ArmourPart.LEFT_ARM);
            buildArmourPart(player, ArmourPart.RIGHT_ARM);
            break;
        case LEGS:
            if (this.skirtMode) {
                buildArmourPart(player, ArmourPart.SKIRT);
                CustomArmourManager.removeCustomArmour(player, type, ArmourPart.LEFT_LEG);
                CustomArmourManager.removeCustomArmour(player, type, ArmourPart.RIGHT_LEG);
            } else {
                buildArmourPart(player, ArmourPart.LEFT_LEG);
                buildArmourPart(player, ArmourPart.RIGHT_LEG);
                CustomArmourManager.removeCustomArmour(player, type, ArmourPart.SKIRT);
            }
            
            break;
        case FEET:
            buildArmourPart(player, ArmourPart.LEFT_FOOT);
            buildArmourPart(player, ArmourPart.RIGHT_FOOT);
            break;
        default:
            ModLogger.log(Level.WARN, "TileEntityArmourerBrain at X:" + xCoord + " Y:" + yCoord +
                    " Z:" + zCoord + " has an invalid armour type.");
            break;
        }
    }
    
    private void buildArmourPart(EntityPlayer player, ArmourPart part) {
        ArrayList<ArmourBlockData> armourBlockData = new ArrayList<ArmourBlockData>();
        
        for (int ix = 0; ix < part.getXSize(); ix++) {
            for (int iy = 0; iy < part.getYSize(); iy++) {
                for (int iz = 0; iz < part.getZSize(); iz++) {
                    addArmourToList(
                            xCoord + xOffset + ix + part.getXOffset(),
                            yCoord + iy + 1 + part.getYOffset(),
                            zCoord + zOffset + iz + part.getZOffset(),
                            ix, iy, iz, armourBlockData, part);
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            CustomArmourData armourData = new CustomArmourData(armourBlockData, type, part);
            CustomArmourManager.addCustomArmour(player, armourData);
        } else {
            CustomArmourManager.removeCustomArmour(player, type, part);
        }
    }
    
    private void addArmourToList(int x, int y, int z, int ix, int iy, int iz, ArrayList<ArmourBlockData> list, ArmourPart armourPart) {
        if (worldObj.isAirBlock(x, y, z)) { return; }
        Block block = worldObj.getBlock(x, y, z);
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
            int colour = UtilBlocks.getColourFromTileEntity(worldObj ,x, y, z);
            ArmourBlockData blockData = new ArmourBlockData(
                    (armourPart.getXSize() / 2 - ix - 1),
                    (armourPart.getYSize() - iy),
                    (iz - armourPart.getZSize() / 2),
                    colour, block == ModBlocks.colourableGlowing);
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
    
    public boolean isShowGuides() {
        return showGuides;
    }
    
    public void setType(ArmourerType type) {
        this.type = type;
        if (formed) {
            removeBoundingBoxed();
            createBoundingBoxes(); 
        }
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setDirection(ForgeDirection direction) {
        this.direction = direction;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setSkirtMode(boolean skirtMode) {
        if (this.worldObj.isRemote) { return; }
        ModLogger.log("Setting skirt mode " + skirtMode);
        this.skirtMode = skirtMode;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        if (type == ArmourerType.LEGS) {
            removeBoundingBoxed();
            createBoundingBoxes(); 
        }
    }
    
    public void toggleGuides() {
        this.showGuides = !this.showGuides;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void toggleSkirtMode() {
        setSkirtMode(!skirtMode);
    }
    
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        writeTeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readBaseFromNBT(compound);
        readTeFromNBT(compound);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readTeFromNBT(compound);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeTeToNBT(compound);
    }
    
    public void readTeFromNBT(NBTTagCompound compound) {
        direction = ForgeDirection.getOrientation(compound.getInteger(TAG_DIRECTION));
        type = ArmourerType.getOrdinal(compound.getInteger(TAG_TYPE));
        formed = compound.getBoolean(TAG_FORMED);
        skirtMode = compound.getBoolean(TAG_SKIRT_MODE);
        xOffset = compound.getInteger(TAG_X_OFFSET);
        zOffset = compound.getInteger(TAG_Z_OFFSET);
        showGuides = compound.getBoolean(TAG_SHOW_GUIDES);
    }
    
    public void writeTeToNBT(NBTTagCompound compound) {
        compound.setInteger(TAG_DIRECTION, direction.ordinal());
        compound.setInteger(TAG_TYPE, type.ordinal());
        compound.setBoolean(TAG_FORMED, formed);
        compound.setBoolean(TAG_SKIRT_MODE, skirtMode);
        compound.setInteger(TAG_X_OFFSET, xOffset);
        compound.setInteger(TAG_Z_OFFSET, zOffset);
        compound.setBoolean(TAG_SHOW_GUIDES, showGuides);
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.ARMOURER_BRAIN;
    }
    
    @Override
    public double getMaxRenderDistanceSquared() {
        return super.getMaxRenderDistanceSquared() * 10;
    }
}
