package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.customarmor.ArmourType;
import riskyken.armourersWorkshop.common.customarmor.ArmourerWorldHelper;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ItemArmourTemplate;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class TileEntityArmourerBrain extends AbstractTileEntityMultiBlockParent {
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_TYPE = "type";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    private static final String TAG_SHOW_OVERLAY = "showOverlay";
    
    private static final String TAG_ARMOUR_DATA = "armourData";
    private static final String TAG_CUSTOM_NAME = "customName";
    
    private GameProfile gameProfile = null;
    private ArmourType type;
    private boolean showGuides;
    private boolean showOverlay;
    
    public TileEntityArmourerBrain() {
        this.type = ArmourType.HEAD;
        this.formed = false;
        this.items = new ItemStack[2];
        this.showOverlay = true;
    }
    
    /**
     * Get blocks in the world and saved them onto an items NBT data.
     * @param player The player that pressed the save button.
     * @param name Custom name for the item.
     */
    public void saveArmourItem(EntityPlayerMP player, String name) {
        if (this.worldObj.isRemote) { return; }
        
        CustomArmourItemData armourItemData;
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOuput = getStackInSlot(1);
        
        if (stackInput == null) { return; }
        if (stackOuput != null) { return; }
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
        ItemStack stackOuput = getStackInSlot(1);
        
        if (stackInput == null) { return; }
        if (stackOuput != null) { return; }
        if (!(stackInput.getItem() instanceof ItemArmourTemplate)) { return; }
        
        if (!stackInput.hasTagCompound()) { return; };
        NBTTagCompound itemNBT = stackInput.getTagCompound();
        
        if (!itemNBT.hasKey(TAG_ARMOUR_DATA)) { return; }
        NBTTagCompound dataNBT = itemNBT.getCompoundTag(TAG_ARMOUR_DATA);
        
        CustomArmourItemData customArmourItemData = new CustomArmourItemData(dataNBT);
        
        ArmourerWorldHelper.loadArmourItem(worldObj, xCoord + xOffset, yCoord + 1, zCoord + zOffset, customArmourItemData);
    
        setInventorySlotContents(0, null);
        setInventorySlotContents(1, stackInput);
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
    
    @Override
    protected void createBoundingBoxes() {
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
    
    @Override
    protected void removeBoundingBoxed() {
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
    public String getInventoryName() {
        return LibBlockNames.ARMOURER_BRAIN;
    }
    
    @Override
    public double getMaxRenderDistanceSquared() {
        return super.getMaxRenderDistanceSquared() * 10;
    }
    
    public Packet getDescriptionPacket() {
        updateProfileData();
        NBTTagCompound compound = new NBTTagCompound();
        writeBaseToNBT(compound);
        writeCommonToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readBaseFromNBT(compound);
        readCommonFromNBT(compound);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCommonFromNBT(compound);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCommonToNBT(compound);
    }
    
    @Override
    public void readCommonFromNBT(NBTTagCompound compound) {
        super.readCommonFromNBT(compound);
        type = ArmourType.getOrdinal(compound.getInteger(TAG_TYPE));
        showGuides = compound.getBoolean(TAG_SHOW_GUIDES);
        showOverlay = compound.getBoolean(TAG_SHOW_OVERLAY);
        if (compound.hasKey(TAG_OWNER, 10)) {
            this.gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
        }
    }
    
    @Override
    public void writeCommonToNBT(NBTTagCompound compound) {
        super.writeCommonToNBT(compound);
        compound.setInteger(TAG_TYPE, type.ordinal());
        compound.setBoolean(TAG_SHOW_GUIDES, showGuides);
        compound.setBoolean(TAG_SHOW_OVERLAY, showOverlay);
        if (this.gameProfile != null) {
            NBTTagCompound profileTag = new NBTTagCompound();
            NBTUtil.func_152460_a(profileTag, this.gameProfile);
            compound.setTag(TAG_OWNER, profileTag);
        }
    }
}
