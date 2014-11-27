package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.BipedRotations;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.UtilBlocks;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class TileEntityMannequin extends AbstractTileEntityInventory {
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_ROTATION = "rotation";
    
    private GameProfile gameProfile = null;
    private MannequinFakePlayer fakePlayer = null;
    
    private EntityEquipmentData equipmentData;
    private BipedRotations bipedRotations;
    private int rotation;
    
    public TileEntityMannequin() {
        equipmentData = new EntityEquipmentData();
        bipedRotations = new BipedRotations();
        bipedRotations.leftArm.rotationZ = (float) Math.toRadians(-10);
        bipedRotations.rightArm.rotationZ = (float) Math.toRadians(10);
        bipedRotations.leftArm.rotationY = (float) Math.toRadians(-1);
        bipedRotations.rightArm.rotationY = (float) Math.toRadians(1);
        this.items = new ItemStack[6];
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if (!worldObj.isRemote) {
            if (itemstack == null) {
                equipmentData.removeEquipment(EnumEquipmentType.getOrdinal(i + 1)); 
                markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else {
                setEquipment(itemstack);
            }
        }
        super.setInventorySlotContents(i, itemstack);
    }
    
    public void setEquipment(ItemStack stack) {
        if (!stack.hasTagCompound()) { return; }
        NBTTagCompound data = stack.getTagCompound();
        if (!data.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return ;}
        NBTTagCompound armourNBT = data.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        int equipmentId = armourNBT.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
        CustomArmourItemData equipmentData = EquipmentDataCache.INSTANCE.getEquipmentData(equipmentId);
        setEquipment(equipmentData.getType(), equipmentId);
    }
    
    public void setOwner(ItemStack stack) {
        if (stack.hasDisplayName()) {
            if (gameProfile == null) {
                setGameProfile(new GameProfile(null, stack.getDisplayName()));
                stack.stackSize--;
            }
        }
    }
    @Override
    public void invalidate() {
        if (!worldObj.isRemote) {
            if (gameProfile != null) {
                ItemStack stack = new ItemStack(Items.name_tag);
                stack.setStackDisplayName(gameProfile.getName());
                float f = 0.7F;
                double xV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double yV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double zV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(worldObj, (double)xCoord + xV, (double)yCoord + yV, (double)zCoord + zV, stack);
                worldObj.spawnEntityInWorld(entityitem);
            }
            UtilBlocks.dropInventoryBlocks(worldObj, this, xCoord, yCoord, zCoord);
        }
        super.invalidate();
    }
    private void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        this.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setEquipment(EnumEquipmentType armourType, int equipmentId) {
        equipmentData.addEquipment(armourType, equipmentId);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public int getRotation() {
        return rotation;
    }
    
    public GameProfile getGameProfile() {
        return gameProfile;
    }
    
    public BipedRotations getBipedRotations() {
        return bipedRotations;
    }
    
    public void setBipedRotations(BipedRotations bipedRotations) {
        this.bipedRotations = bipedRotations;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public MannequinFakePlayer getFakePlayer() {
        return fakePlayer;
    }
    
    public void setFakePlayer(MannequinFakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
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
    public void readCommonFromNBT(NBTTagCompound compound) {
        super.readCommonFromNBT(compound);
        equipmentData.loadNBTData(compound);
        bipedRotations.loadNBTData(compound);
        this.rotation = compound.getInteger(TAG_ROTATION);
        if (compound.hasKey(TAG_OWNER, 10)) {
            this.gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
        }
    }
    
    @Override
    public void writeCommonToNBT(NBTTagCompound compound) {
        super.writeCommonToNBT(compound);
        equipmentData.saveNBTData(compound);
        bipedRotations.saveNBTData(compound);
        compound.setInteger(TAG_ROTATION, this.rotation);
        if (this.gameProfile != null) {
            NBTTagCompound profileTag = new NBTTagCompound();
            NBTUtil.func_152460_a(profileTag, this.gameProfile);
            compound.setTag(TAG_OWNER, profileTag);
        }
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
    
    public EntityEquipmentData getEquipmentData() {
        return equipmentData;
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 3, zCoord + 1);
        return bb;
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.MANNEQUIN;
    }
}
