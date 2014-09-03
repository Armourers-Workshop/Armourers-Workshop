package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.ArmourerWorldHelper;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourData;
import riskyken.armourersWorkshop.common.items.ItemArmourTemplate;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.ModLogger;

public class TileEntityArmourerBrain extends AbstractTileEntityInventory {

    public static final int MULTI_BLOCK_SIZE = 22;
    private static final long TICK_COOLDOWN = 40L;
    private static final long TICK_OFFSET = 5L;
    
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_TYPE = "type";
    private static final String TAG_FORMED = "formed";
    private static final String TAG_RECHECK = "recheck";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    private static final String TAG_ARMOUR_DATA = "armourData";
    private static final String TAG_CUSTOM_NAME = "customName";
    
    private ForgeDirection direction;
    private ArmourerType type;
    private boolean formed;
    private boolean loaded;
    private boolean recheck;
    private int xOffset;
    private int zOffset;
    private boolean showGuides;
    
    public TileEntityArmourerBrain() {
        this.direction = ForgeDirection.UNKNOWN;
        this.type = ArmourerType.NONE;
        this.formed = false;
        this.items = new ItemStack[2];
    }
    
    public void saveArmourItem(EntityPlayerMP player, String name) {
        if (this.worldObj.isRemote) { return; }
        
        ArrayList<CustomArmourData> armourData;
        ItemStack stackInput = getStackInSlot(0);
        
        if (stackInput == null) { return; }
        if (!(stackInput.getItem() instanceof ItemArmourTemplate)) { return; }
        
        
        armourData = ArmourerWorldHelper.buildArmourItem(worldObj, type, player, xCoord + xOffset, yCoord + 1, zCoord + zOffset);
        
        NBTTagCompound dataNBT = new NBTTagCompound();
        
        if (!name.equals("")) {
            dataNBT.setString(TAG_CUSTOM_NAME, name);
        }
        
        for (int i = 0; i < armourData.size(); i++) {
            CustomArmourData data = armourData.get(i);
            String key = data.getArmourType().name() + ":" + data.getArmourPart().name();
            NBTTagCompound partNBT = new NBTTagCompound();
            ModLogger.log(key);
            data.writeToNBT(partNBT);
            dataNBT.setTag(key, partNBT);
        }
        
        if (!stackInput.hasTagCompound()) {
            stackInput.setTagCompound(new NBTTagCompound());
        }
        
        stackInput.getTagCompound().setTag(TAG_ARMOUR_DATA, dataNBT);
    }

    public void loadArmourItem() {
        // TODO Auto-generated method stub
        
    }
    
    public void childUpdate() {
        recheck = true;
    }
    
    @Override
    public void setInventorySlotContents(int slotId, ItemStack stack) {
        super.setInventorySlotContents(slotId, stack);
        if (loaded) {
            checkForTemplateItem();
        }
    }
    
    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) {return; }
        if (!this.loaded) { this.loaded = true; }
        if (this.formed & !this.recheck) { return; }
        if ((this.worldObj.getTotalWorldTime() + this.TICK_OFFSET) % TICK_COOLDOWN != 0L) { return; }
        checkForValidMultiBlock();
    }
    
    private void checkForTemplateItem() {
        if (this.worldObj.isRemote) { return; }
        ItemStack stackInput = getStackInSlot(0);
        if (stackInput == null) {
            setType(ArmourerType.NONE);
            return;
        }
        
        if (stackInput.getItem() instanceof ItemArmourTemplate) {
            setType(ArmourerType.getOrdinal(stackInput.getItemDamage() + 1));
        }
    }
    
    public boolean checkForValidMultiBlock() {
        if (formed) {
            removeOldChildren();
            removeBoundingBoxed();
            formed = false;
            recheck = false;
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        
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
        
        formed = true;
        createBoundingBoxes();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }
    
    private void createBoundingBoxes() {
        switch (type) {
        case NONE:
            break;
        case HEAD:
            for (int ix = 0; ix < 8; ix++) {
                for (int iy = 0; iy < 8; iy++) {
                    for (int iz = 0; iz < 8; iz++) {
                        createBoundingBox(xCoord + xOffset + ix + 7, yCoord + iy + 2, zCoord + zOffset + iz + 7);
                    }
                }
            } 
            break;
        case CHEST:
            //Chest
            for (int ix = 0; ix < 8; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        createBoundingBox(xCoord + xOffset + ix + 7, yCoord + iy + 2, zCoord + zOffset + iz + 4);
                    }
                }
            } 
            for (int ix = 0; ix < 4; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        //Right Arm
                        createBoundingBox(xCoord + xOffset + ix + 4, yCoord + iy + 2, zCoord + zOffset + iz + 14);
                        //Left Arm
                        createBoundingBox(xCoord + xOffset + ix + 14, yCoord + iy + 2, zCoord + zOffset + iz + 14);
                    }
                }
            }
            break;
        case LEGS:
            for (int ix = 0; ix < 4; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        //Right Leg
                        createBoundingBox(xCoord + xOffset + ix + 4, yCoord + iy + 2, zCoord + zOffset + iz + 9);
                        //Left Leg
                        createBoundingBox(xCoord + xOffset + ix + 14, yCoord + iy + 2, zCoord + zOffset + iz + 9);
                    }
                }
            }
            break;
        case SKIRT:
            for (int ix = 0; ix < 4; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        //Right Leg
                        createBoundingBox(xCoord + xOffset + ix + 7, yCoord + iy + 2, zCoord + zOffset + iz + 9);
                        //Left Leg
                        createBoundingBox(xCoord + xOffset + ix + 11, yCoord + iy + 2, zCoord + zOffset + iz + 9);
                    }
                }
            }
            break;
        case FEET:
            for (int ix = 0; ix < 4; ix++) {
                for (int iy = 0; iy < 12; iy++) {
                    for (int iz = 0; iz < 4; iz++) {
                        
                        //Right Leg
                        createBoundingBox(xCoord + xOffset + ix + 4, yCoord + iy + 2, zCoord + zOffset + iz + 9);
                        //Left Leg
                        createBoundingBox(xCoord + xOffset + ix + 14, yCoord + iy + 2, zCoord + zOffset + iz + 9);
                    }
                }
            }
            break;
        }
    }
    
    private void createBoundingBox(int x, int y, int z) {
        if (worldObj.isAirBlock(x, y, z)) {
            worldObj.setBlock(x, y, z, ModBlocks.boundingBox);
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
                        worldObj.setBlockToAir(xCoord + xOffset + ix, yCoord + 1, zCoord + zOffset + iy);
                        //worldObj.setBlock(xCoord + xOffset + ix, yCoord + 1, zCoord + zOffset + iy, Blocks.glass);
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

    public ArmourerType getType() {
        return type;
    }
    
    public boolean isFormed() {
        return formed;
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
    
    private void setType(ArmourerType type) {
        if (this.type == type) { return; }
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
    
    public void toggleGuides() {
        this.showGuides = !this.showGuides;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        recheck = compound.getBoolean(TAG_RECHECK);
        xOffset = compound.getInteger(TAG_X_OFFSET);
        zOffset = compound.getInteger(TAG_Z_OFFSET);
        showGuides = compound.getBoolean(TAG_SHOW_GUIDES);
    }
    
    public void writeTeToNBT(NBTTagCompound compound) {
        compound.setInteger(TAG_DIRECTION, direction.ordinal());
        compound.setInteger(TAG_TYPE, type.ordinal());
        compound.setBoolean(TAG_FORMED, formed);
        compound.setBoolean(TAG_RECHECK, recheck);
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
