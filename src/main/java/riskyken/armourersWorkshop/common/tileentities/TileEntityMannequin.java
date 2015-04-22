package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.BipedRotations;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.data.EquipmentSkinTypeData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeHelper;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper.SkinNBTData;
import riskyken.armourersWorkshop.utils.GameProfileUtils;
import riskyken.armourersWorkshop.utils.GameProfileUtils.IGameProfileCallback;
import riskyken.armourersWorkshop.utils.UtilBlocks;

import com.mojang.authlib.GameProfile;

public class TileEntityMannequin extends AbstractTileEntityInventory implements IGameProfileCallback {
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_ROTATION = "rotation";
    private static final String TAG_IS_DOLL = "isDoll";
    private static final String TAG_HEIGHT_OFFSET = "heightOffset";
    
    private GameProfile gameProfile = null;
    private GameProfile newProfile = null;
    private MannequinFakePlayer fakePlayer = null;
    
    private EntityEquipmentData equipmentData;
    private BipedRotations bipedRotations;
    private int rotation;
    private boolean isDoll;
    private int heightOffset;
    
    public TileEntityMannequin() {
        this(false);
    }
    
    public TileEntityMannequin(boolean isDoll) {
        equipmentData = new EntityEquipmentData();
        bipedRotations = new BipedRotations();
        bipedRotations.leftArm.rotationZ = (float) Math.toRadians(-10);
        bipedRotations.rightArm.rotationZ = (float) Math.toRadians(10);
        bipedRotations.leftArm.rotationY = (float) Math.toRadians(-1);
        bipedRotations.rightArm.rotationY = (float) Math.toRadians(1);
        this.items = new ItemStack[7];
        this.isDoll = isDoll;
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if (!worldObj.isRemote) {
            if (itemstack == null) {
                IEquipmentSkinType skinType = SkinTypeHelper.getSkinTypeForSlot(i);
                if (skinType != null) {
                    equipmentData.removeEquipment(skinType);
                    markDirty();
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            } else {
                setEquipment(itemstack);
            }
        }
        super.setInventorySlotContents(i, itemstack);
    }
    
    public void setEquipment(ItemStack stack) {
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinNBTData skinData = EquipmentNBTHelper.getSkinNBTDataFromStack(stack);
            EquipmentSkinTypeData equipmentData = EquipmentDataCache.INSTANCE.getEquipmentData(skinData.skinId);
            setEquipment(equipmentData.getSkinType(), skinData.skinId);
        }
    }
    
    public void setOwner(ItemStack stack) {
        if (stack.hasDisplayName()) {
            if (gameProfile == null) {
                setGameProfile(new GameProfile(null, stack.getDisplayName()));
                stack.stackSize--;
                updateProfileData();
            }
        }
    }
    
    public boolean getIsDoll() {
        return isDoll;
    }
    
    @Override
    public void invalidate() {
        if (!worldObj.isRemote) {
            ItemStack stack = new ItemStack(ModBlocks.mannequin);
            if (isDoll) {
                stack = new ItemStack(ModBlocks.doll);
            }
            if (gameProfile != null) {
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.func_152460_a(profileTag, gameProfile);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag(TAG_OWNER, profileTag);
                //stack.setStackDisplayName(gameProfile.getName());
            }
            float f = 0.7F;
            double xV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double yV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double zV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(worldObj, (double)xCoord + xV, (double)yCoord + yV, (double)zCoord + zV, stack);
            worldObj.spawnEntityInWorld(entityitem);
            UtilBlocks.dropInventoryBlocks(worldObj, this, xCoord, yCoord, zCoord);
        }
        super.invalidate();
    }
    
    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        if (!worldObj.isRemote) {
            updateProfileData();
        }
    }
    
    public void setEquipment(IEquipmentSkinType skinType, int equipmentId) {
        equipmentData.addEquipment(skinType, equipmentId);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public int getHeightOffset() {
        return heightOffset;
    }
    
    public void setHeightOffset(int heightOffset) {
        this.heightOffset = heightOffset;
    }
    
    private void updateHeightOffset() {
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
        updateHeightOffset();
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
        GameProfileUtils.updateProfileData(gameProfile, this);
    }
    
    @Override
    public void readCommonFromNBT(NBTTagCompound compound) {
        super.readCommonFromNBT(compound);
        equipmentData.loadNBTData(compound);
        bipedRotations.loadNBTData(compound);
        this.isDoll = compound.getBoolean(TAG_IS_DOLL);
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
        compound.setBoolean(TAG_IS_DOLL, this.isDoll);
        compound.setInteger(TAG_ROTATION, this.rotation);
        if (this.newProfile != null) {
            this.gameProfile = newProfile;
            this.newProfile = null;
        }
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

    @Override
    public void profileUpdated(GameProfile gameProfile) {
        newProfile = gameProfile;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
