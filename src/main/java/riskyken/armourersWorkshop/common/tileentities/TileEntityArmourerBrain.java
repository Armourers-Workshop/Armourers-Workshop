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
import riskyken.armourersWorkshop.api.common.equipment.EnumBodyPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.equipment.ArmourerWorldHelper;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class TileEntityArmourerBrain extends AbstractTileEntityInventory {
    
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_TYPE = "type";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    private static final String TAG_SHOW_OVERLAY = "showOverlay";
    private static final String TAG_CUSTOM_NAME = "customeName";
    private static final int HEIGHT_OFFSET = 2;
    
    private ForgeDirection direction;
    private GameProfile gameProfile = null;
    private EnumEquipmentType type;
    private boolean showGuides;
    private boolean showOverlay;
    private String customName;
    
    public TileEntityArmourerBrain() {
        this.type = EnumEquipmentType.HEAD;
        this.items = new ItemStack[2];
        this.showOverlay = true;
        this.customName = "";
    }
    
    /**
     * Get blocks in the world and saved them onto an items NBT data.
     * @param player The player that pressed the save button.
     * @param name Custom name for the item.
     */
    public void saveArmourItem(EntityPlayerMP player, String name) {
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
        if (!(stackInput.getItem() instanceof ItemEquipmentSkinTemplate)) {
            return;
        }
        
        String authorName = player.getCommandSenderName();
        String customName = name;
        
        CustomEquipmentItemData armourItemData;
        armourItemData = ArmourerWorldHelper.saveArmourItem(worldObj, type, authorName, customName,
                xCoord, yCoord + HEIGHT_OFFSET, zCoord, direction);
        
        if (armourItemData == null) {
            return;
        }
        
        stackOutput = EquipmentNBTHelper.makeStackForEquipment(armourItemData);
        
        this.decrStackSize(0, 1);
        setInventorySlotContents(1, stackOutput);
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

        if (stackInput.getItemDamage() != type.ordinal() - 1) {
            return;
        }

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
    
    private void checkForTemplateItem() {
        if (this.worldObj.isRemote) { return; }
        ItemStack stackInput = getStackInSlot(0);
        if (stackInput == null) {
            setType(EnumEquipmentType.NONE);
            return;
        }
        
        if (stackInput.getItem() instanceof ItemEquipmentSkinTemplate) {
            setType(EnumEquipmentType.getOrdinal(stackInput.getItemDamage() + 1));
        }
    }
    
    public int getHeightOffset() {
        return HEIGHT_OFFSET;
    }

    public void clearArmourCubes() {
        for (int i = 0; i < type.getParts().length; i++) {
            EnumEquipmentPart part = type.getParts()[i];
            ModLogger.log("Clearing " + part);
            for (int ix = 0; ix <  part.getTotalXSize(); ix++) {
                for (int iy = 0; iy < part.getTotalYSize(); iy++) {
                    for (int iz = 0; iz <  part.getTotalZSize(); iz++) {
                        int tarX = xCoord + part.getStartX() - part.xLocation + ix;
                        int tarY = yCoord + part.getStartY() + getHeightOffset() + part.yLocation + iy;
                        int tarZ = zCoord + part.getStartZ() - part.zLocation + iz;
                        Block tarBlock = worldObj.getBlock(tarX, tarY, tarZ);
                        if (tarBlock == ModBlocks.colourable | tarBlock == ModBlocks.colourableGlowing) {
                            worldObj.setBlockToAir(tarX, tarY, tarZ);
                        }
                    }
                }
            }
        }
    }
    
    public void cloneToSide(ForgeDirection side) {
        /*
        int ySize = MULTI_BLOCK_SIZE;
        if (type == EnumEquipmentType.WEAPON) {
            ySize += 20;
        }
        for (int ix = 0; ix < MULTI_BLOCK_SIZE / 2; ix++) {
            for (int iy = 1; iy < ySize - 1; iy++) {
                for (int iz = 1; iz < MULTI_BLOCK_SIZE; iz++) {
                    int x = xCoord + xOffset + ix;
                    int newX = xCoord + xOffset + MULTI_BLOCK_SIZE - ix - 1;
                    if (side == ForgeDirection.EAST) {
                        x += MULTI_BLOCK_SIZE / 2;
                        newX = xCoord + xOffset + (MULTI_BLOCK_SIZE / 2) - ix - 1;
                    }
                    int y = yCoord + iy;
                    int z = zCoord + zOffset + iz;
                    
                    if (!worldObj.isAirBlock(x, y, z)) {
                        Block block = worldObj.getBlock(x, y, z);
                        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
                            TileEntity te1 = worldObj.getTileEntity(x, y, z);
                            worldObj.setBlock(newX, y, z, block);
                            if (te1 != null && te1 instanceof IPantable) {
                                TileEntity te3 = worldObj.getTileEntity(newX, y, z);
                                if (te3 != null && te3 instanceof IPantable) {
                                    ((IPantable)te3).setColour(((IPantable)te1).getColour());
                                } else {
                                    TileEntityColourable te2 = new TileEntityColourable(((IPantable)te1).getColour());
                                    worldObj.setTileEntity(newX, y, z, te2);
                                }
                            }
                        }
                    }
                }
            }
        }
        */
    }
    
    protected void removeBoundingBoxed() {
        EnumEquipmentPart[] parts = type.getParts();
        for (int i = 0; i < parts.length; i++) {
            EnumEquipmentPart part = parts[i];
            removeBoundingBoxesForPart(part);
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
        EnumEquipmentPart[] parts = type.getParts();
        for (int i = 0; i < parts.length; i++) {
            EnumEquipmentPart part = parts[i];
            createBoundingBoxesForPart(part);
        }
    }
    
    private void createBoundingBoxesForPart(EnumEquipmentPart part) {
        for (int ix = 0; ix < part.xSize; ix++) {
            for (int iy = 0; iy < part.ySize; iy++) {
                for (int iz = 0; iz < part.zSize; iz++) {
                    int x = xCoord - part.xLocation - (part.xSize / 2) + ix;
                    int y = yCoord + part.yLocation + getHeightOffset() + iy;
                    int z = zCoord + part.zLocation - (part.zSize / 2) + iz;
                    if (part == EnumEquipmentPart.WEAPON | part == EnumEquipmentPart.BOW) {
                        z += 4;
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
        
        bb = AxisAlignedBB.getBoundingBox(xCoord - 10, yCoord - 10, zCoord - 10,
                xCoord + 20, yCoord + 40 + 20, zCoord + 20);
        
        return bb;
    }

    public EnumEquipmentType getType() {
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
    
    public void setType(EnumEquipmentType type) {
        if (this.type == type) {
            return;
        }
        removeBoundingBoxed();
        this.type = type;
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
        type = EnumEquipmentType.getOrdinal(compound.getInteger(TAG_TYPE));
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
        compound.setInteger(TAG_TYPE, type.ordinal());
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
