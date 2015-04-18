package riskyken.armourersWorkshop.common.tileentities;

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
import riskyken.armourersWorkshop.api.common.equipment.EnumBodyPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.equipment.ArmourerWorldHelper;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.ISkinHolder;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class TileEntityArmourerBrain extends AbstractTileEntityInventory {
    
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_TYPE = "skinType";
    private static final String TAG_TYPE_OLD = "type";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    private static final String TAG_SHOW_OVERLAY = "showOverlay";
    private static final String TAG_CUSTOM_NAME = "customeName";
    private static final int HEIGHT_OFFSET = 1;
    
    private ForgeDirection direction;
    private GameProfile gameProfile = null;
    private ISkinType skinType;
    private boolean showGuides;
    private boolean showOverlay;
    private String customName;
    
    public TileEntityArmourerBrain() {
        this.skinType = SkinTypeRegistry.INSTANCE.getSkinFromRegistryName("armourers:head");
        this.items = new ItemStack[2];
        this.showOverlay = true;
        this.showGuides = true;
        this.customName = "";
    }
    
    /**
     * Get blocks in the world and saved them onto an items NBT data.
     * @param player The player that pressed the save button.
     * @param name Custom name for the item.
     */
    public void saveArmourItem(EntityPlayerMP player, String name, String tags) {
        if (this.worldObj.isRemote) {
            return;
        }
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        if (stackOutput != null) {
            return;
        }
        if (!(stackInput.getItem() instanceof ISkinHolder)) {
            return;
        }
        ISkinHolder inputItem = (ISkinHolder)stackInput.getItem();
        
        String authorName = player.getCommandSenderName();
        String customName = name;
        
        CustomEquipmentItemData armourItemData;
        
        //TODO Save skins.
        /*
        armourItemData = ArmourerWorldHelper.saveArmourItem(worldObj, type, authorName, customName, tags,
                xCoord, yCoord + HEIGHT_OFFSET, zCoord, direction);
        
        if (armourItemData == null) {
            return;
        }
        
        stackOutput = inputItem.makeStackForEquipment(armourItemData);
        if (stackOutput == null) {
            return;
        }
        
        this.decrStackSize(0, 1);
        setInventorySlotContents(1, stackOutput);
        */
    }

    /**
     * Reads the NBT data from an item and places blocks in the world.
     * @param player The player that pressed the load button.
     */
    public void loadArmourItem(EntityPlayerMP player) {
        if (this.worldObj.isRemote) {
            return;
        }
        ItemStack stackInput = this.getStackInSlot(0);
        ItemStack stackOuput = this.getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        if (stackOuput != null) {
            return;
        }
        if (!(stackInput.getItem() instanceof ItemEquipmentSkin)) {
            return;
        }
        
        if (!EquipmentNBTHelper.itemStackHasCustomEquipment(stackInput)) {
            return;
        }

        //TODO Check type when loading skin.
        /*
        if (stackInput.getItemDamage() != type.ordinal() - 1) {
            return;
        }
         */
        
        int equipmentId = EquipmentNBTHelper.getEquipmentIdFromStack(stackInput);
        CustomEquipmentItemData equipmentData = EquipmentDataCache.INSTANCE.getEquipmentData(equipmentId);
        setCustomName(equipmentData.getCustomName());
        
        ArmourerWorldHelper.loadArmourItem(worldObj, xCoord, yCoord + HEIGHT_OFFSET, zCoord, equipmentData, direction);
    
        this.setInventorySlotContents(0, null);
        this.setInventorySlotContents(1, stackInput);
    }
    
    public void onPlaced() {
        createBoundingBoxes();
    }
    
    public void preRemove() {
        removeBoundingBoxed();
    }
    
    public int getHeightOffset() {
        return HEIGHT_OFFSET;
    }

    public void clearArmourCubes() {
        if (skinType != null) {
            skinType.clearArmourCubes(worldObj, xCoord, yCoord + getHeightOffset(), zCoord);
        }
    }
    
    protected void removeBoundingBoxed() {
        if (skinType != null) {
            skinType.removeBoundingBoxed(worldObj, xCoord, yCoord + getHeightOffset(), zCoord);
        }
    }
    
    private void removeBoundingBoxesForPart(EnumEquipmentPart part) {
        for (int ix = 0; ix < part.xSize; ix++) {
            for (int iy = 0; iy < part.ySize; iy++) {
                for (int iz = 0; iz < part.zSize; iz++) {
                    int x = xCoord - part.xLocation - (part.xSize / 2) + ix;
                    int y = yCoord + part.yLocation + getHeightOffset() + iy;
                    int z = zCoord + part.zLocation  - (part.zSize / 2) + iz;
                    if (part == EnumEquipmentPart.WEAPON | part == EnumEquipmentPart.BOW) {
                        z += 4;
                    }
                    if (worldObj.getBlock(x, y, z) == ModBlocks.boundingBox) {
                        worldObj.setBlockToAir(x, y, z);
                    }
                }
            }
        }
    }
    
    protected void createBoundingBoxes() {
        if (skinType != null) {
            skinType.createBoundingBoxes(worldObj, xCoord, yCoord + getHeightOffset(), zCoord);
        }
    }
    
    private void createBoundingBoxesForPart(EnumEquipmentPart part) {
        int xSize = part.xSize;
        int ySize = part.ySize;
        int zSize = part.zSize;
        if (part == EnumEquipmentPart.WEAPON | part == EnumEquipmentPart.BOW) {
            zSize -= 4;
        }
        for (int ix = 0; ix < xSize; ix++) {
            for (int iy = 0; iy < ySize; iy++) {
                for (int iz = 0; iz < zSize; iz++) {
                    int x = xCoord - part.xLocation - (part.xSize / 2) + ix;
                    int y = yCoord + part.yLocation + getHeightOffset() + iy;
                    int z = zCoord + part.zLocation - (part.zSize / 2) + iz;
                    if (part == EnumEquipmentPart.WEAPON | part == EnumEquipmentPart.BOW) {
                        z += 8;
                    }
                    if (part == EnumEquipmentPart.SKIRT & ix > 3) {
                        createBoundingBox(x, y, z, EnumBodyPart.RIGHT_LEG);
                    } else {
                        createBoundingBox(x, y, z, part.bodyPart);
                    }
                }
            }
        }
    }
    
    private void createBoundingBox(int x, int y, int z, EnumBodyPart bodyPart) {
        if (worldObj.isAirBlock(x, y, z)) {
            worldObj.setBlock(x, y, z, ModBlocks.boundingBox);
            TileEntity te = null;
            te = worldObj.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityBoundingBox) {
                ((TileEntityBoundingBox)te).setParent(xCoord, yCoord, zCoord, bodyPart);
            } else {
                te = new TileEntityBoundingBox(xCoord, yCoord, zCoord, bodyPart);
                worldObj.setTileEntity(x, y, z, te);
            }
        }
    }
    
    public void setDirection(ForgeDirection direction) {
        this.direction = direction;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public ForgeDirection getDirection() {
        return direction;
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = super.getRenderBoundingBox();
        
        bb = AxisAlignedBB.getBoundingBox(xCoord - 10, yCoord - 10, zCoord - 18,
                xCoord + 20, yCoord + 40 + 20, zCoord + 20);
        
        return bb;
    }

    public ISkinType getSkinType() {
        return skinType;
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
    
    public void setSkinType(ISkinType skinType) {
        if (this.skinType == skinType) {
            return;
        }
        removeBoundingBoxed();
        this.skinType = skinType;
        createBoundingBoxes(); 
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        updateProfileData();
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
    
    public String getCustomName() {
        return customName;
    }
    
    public void setCustomName(String customName) {
        this.customName = customName;
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
        direction = ForgeDirection.getOrientation(compound.getByte(TAG_DIRECTION));
        skinType = SkinTypeRegistry.INSTANCE.getSkinFromRegistryName(compound.getString(TAG_TYPE));
        //Update code for old saves
        if (skinType == null && compound.hasKey(TAG_TYPE_OLD)) {
            skinType = SkinTypeRegistry.INSTANCE.getSkinFromLegacyId(compound.getInteger(TAG_TYPE_OLD) - 1);
        }
        showGuides = compound.getBoolean(TAG_SHOW_GUIDES);
        showOverlay = compound.getBoolean(TAG_SHOW_OVERLAY);
        customName = compound.getString(TAG_CUSTOM_NAME);
        if (compound.hasKey(TAG_OWNER, 10)) {
            this.gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
        }
    }
    
    @Override
    public void writeCommonToNBT(NBTTagCompound compound) {
        super.writeCommonToNBT(compound);
        compound.setByte(TAG_DIRECTION, (byte) direction.ordinal());
        if (skinType != null) {
            compound.setString(TAG_TYPE, skinType.getRegistryName());
        }
        compound.setBoolean(TAG_SHOW_GUIDES, showGuides);
        compound.setBoolean(TAG_SHOW_OVERLAY, showOverlay);
        compound.setString(TAG_CUSTOM_NAME, customName);
        if (this.gameProfile != null) {
            NBTTagCompound profileTag = new NBTTagCompound();
            NBTUtil.func_152460_a(profileTag, this.gameProfile);
            compound.setTag(TAG_OWNER, profileTag);
        }
    }
}
