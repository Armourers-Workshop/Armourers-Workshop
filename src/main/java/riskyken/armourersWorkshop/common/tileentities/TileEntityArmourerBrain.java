package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.customarmor.ArmourType;
import riskyken.armourersWorkshop.common.customarmor.ArmourerWorldHelper;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ItemArmourTemplate;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class TileEntityArmourerBrain extends AbstractTileEntityInventory {

    public static final int MULTI_BLOCK_SIZE = 22;
    private static final long TICK_COOLDOWN = 40L;
    private static final long TICK_OFFSET = 5L;
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_TYPE = "type";
    private static final String TAG_FORMED = "formed";
    private static final String TAG_RECHECK = "recheck";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    private static final String TAG_SHOW_OVERLAY = "showOverlay";
    
    private static final String TAG_ARMOUR_DATA = "armourData";
    private static final String TAG_CUSTOM_NAME = "customName";
    
    
    private GameProfile gameProfile = null;
    private ForgeDirection direction;
    private ArmourType type;
    private boolean formed;
    private boolean loaded;
    private boolean recheck;
    private int xOffset;
    private int zOffset;
    private boolean showGuides;
    private boolean showOverlay;
    
    public TileEntityArmourerBrain() {
        this.direction = ForgeDirection.UNKNOWN;
        this.type = ArmourType.HEAD;
        this.formed = false;
        this.items = new ItemStack[2];
        this.showOverlay = true;
    }
    
    //Choccie_Bunny
    //RiskyKen
    //Borro55
    //Kihira
    
    /**
     * Get blocks in the world and saved them onto an items NBT data.
     * @param player The player that pressed the save button.
     * @param name Custom name for the item.
     */
    public void saveArmourItem(EntityPlayerMP player, String name) {
        if (this.worldObj.isRemote) { return; }
        
        CustomArmourItemData armourItemData;
        ItemStack stackInput = getStackInSlot(0);
        
        if (stackInput == null) { return; }
        if (!(stackInput.getItem() instanceof ItemArmourTemplate)) { return; }
        if (ItemArmourTemplate.getArmourType(stackInput) != ArmourType.NONE) { return; }
        
        
        
        armourItemData = ArmourerWorldHelper.saveArmourItem(worldObj, type, player, xCoord + xOffset, yCoord + 1, zCoord + zOffset);
        
        if (armourItemData == null) { return; }
        
        ItemArmourTemplate.setArmourType(this.type, stackInput);
        
        NBTTagCompound itemNBT = new NBTTagCompound();
        
        NBTTagCompound armourNBT = new NBTTagCompound();
        armourItemData.writeToNBT(armourNBT);
        itemNBT.setTag(TAG_ARMOUR_DATA, armourNBT);
        
        
        if (!name.equals("")) {
            itemNBT.setString(TAG_CUSTOM_NAME, name);
        }
        
        stackInput.setTagCompound(itemNBT);
        
        setInventorySlotContents(0, null);
        setInventorySlotContents(1, stackInput);
    }

    /**
     * Reads the NBT data from an item and places blocks in the world.
     * @param player The player that pressed the load button.
     */
    public void loadArmourItem(EntityPlayerMP player) {
        if (this.worldObj.isRemote) { return; }
        ItemStack stackInput = getStackInSlot(0);
        
        if (!stackInput.hasTagCompound()) { return; };
        NBTTagCompound itemNBT = stackInput.getTagCompound();
        
        if (!itemNBT.hasKey(TAG_ARMOUR_DATA)) { return; }
        NBTTagCompound dataNBT = itemNBT.getCompoundTag(TAG_ARMOUR_DATA);
        
        CustomArmourItemData customArmourItemData = new CustomArmourItemData(dataNBT);
        
        ArmourerWorldHelper.loadArmourItem(worldObj, xCoord + xOffset, yCoord + 1, zCoord + zOffset, customArmourItemData);
    
        setInventorySlotContents(0, null);
        setInventorySlotContents(1, stackInput);
    }
    
    /**
     * Called when one of the child blocks is broken.
     */
    public void childUpdate() {
        recheck = true;
    }
    
    @Override
    public void setInventorySlotContents(int slotId, ItemStack stack) {
        super.setInventorySlotContents(slotId, stack);
        if (loaded) {
            //checkForTemplateItem();
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
            setType(ArmourType.NONE);
            return;
        }
        
        if (stackInput.getItem() instanceof ItemArmourTemplate) {
            setType(ArmourType.getOrdinal(stackInput.getItemDamage() + 1));
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
            //TODO Set tile entity here.
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
        AxisAlignedBB bb = super.getRenderBoundingBox();
        
        if (formed) {
            bb = AxisAlignedBB.getBoundingBox(xCoord + xOffset, yCoord, zCoord + zOffset,
                    xCoord + MULTI_BLOCK_SIZE, yCoord + MULTI_BLOCK_SIZE, zCoord + MULTI_BLOCK_SIZE);
        }
        
        return bb;
    }

    public ArmourType getType() {
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
    
    public boolean isShowOverlay() {
        return showOverlay;
    }
    
    public GameProfile getGameProfile() {
        return gameProfile;
    }
    
    public void setType(ArmourType type) {
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
    
    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void toggleGuides() {
        this.showGuides = !this.showGuides;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void toggleOverlay() {
        this.showOverlay = !this.showOverlay;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public Packet getDescriptionPacket() {
        updateProfileData();
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        writeTeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }

    private void updateProfileData(){
        if (this.gameProfile != null && !StringUtils.isNullOrEmpty(this.gameProfile.getName())) {
            if (!this.gameProfile.isComplete() || !this.gameProfile.getProperties().containsKey("textures")) {
                GameProfile gameprofile = MinecraftServer.getServer().func_152358_ax().func_152655_a(this.gameProfile.getName());
                if (gameprofile != null) {
                    Property property = (Property)Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);
                    if (property == null) {
                        gameprofile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(gameprofile, true);
                    }
                    this.gameProfile = gameprofile;
                    this.markDirty();
                }
            }
        }
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
        type = ArmourType.getOrdinal(compound.getInteger(TAG_TYPE));
        formed = compound.getBoolean(TAG_FORMED);
        recheck = compound.getBoolean(TAG_RECHECK);
        xOffset = compound.getInteger(TAG_X_OFFSET);
        zOffset = compound.getInteger(TAG_Z_OFFSET);
        showGuides = compound.getBoolean(TAG_SHOW_GUIDES);
        showOverlay = compound.getBoolean(TAG_SHOW_OVERLAY);
        if (compound.hasKey(TAG_OWNER, 10)) {
            this.gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
        }
    }
    
    public void writeTeToNBT(NBTTagCompound compound) {
        compound.setInteger(TAG_DIRECTION, direction.ordinal());
        compound.setInteger(TAG_TYPE, type.ordinal());
        compound.setBoolean(TAG_FORMED, formed);
        compound.setBoolean(TAG_RECHECK, recheck);
        compound.setInteger(TAG_X_OFFSET, xOffset);
        compound.setInteger(TAG_Z_OFFSET, zOffset);
        compound.setBoolean(TAG_SHOW_GUIDES, showGuides);
        compound.setBoolean(TAG_SHOW_OVERLAY, showOverlay);
        if (this.gameProfile != null) {
            NBTTagCompound profileTag = new NBTTagCompound();
            NBTUtil.func_152460_a(profileTag, this.gameProfile);
            compound.setTag(TAG_OWNER, profileTag);
        }
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
