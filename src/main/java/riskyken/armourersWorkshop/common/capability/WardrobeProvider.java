package riskyken.armourersWorkshop.common.capability;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.inventory.IInventorySlotUpdate;
import riskyken.armourersWorkshop.common.inventory.WardrobeInventory;
import riskyken.armourersWorkshop.common.inventory.WardrobeInventoryContainer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinInfoUpdate;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinWardrobeUpdate;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class WardrobeProvider implements ICapabilitySerializable, IWardrobeCapability, IInventorySlotUpdate {
    
    @CapabilityInject(IWardrobeCapability.class)
    private static final Capability<IWardrobeCapability> WARDROBE_CAP = null;
    
    public static final int MAX_SLOTS_PER_SKIN_TYPE = 5;
    public static final String TAG_EXT_PROP_NAME = "playerCustomEquipmentData";
    private static final String TAG_LAST_XMAS_YEAR = "lastXmasYear";
    private static final ISkinType[] validSkins = {
            SkinTypeRegistry.skinHead,
            SkinTypeRegistry.skinChest,
            SkinTypeRegistry.skinLegs,
            SkinTypeRegistry.skinFeet,
            SkinTypeRegistry.skinSword,
            SkinTypeRegistry.skinBow,
            SkinTypeRegistry.skinArrow
            };
    
    private final WardrobeInventoryContainer wardrobeInventoryContainer;
    private final EntityEquipmentData equipmentData;
    private final EntityPlayer player;
    private EquipmentWardrobeData equipmentWardrobeData = new EquipmentWardrobeData(); 
    public int lastXmasYear;
    private boolean allowNetworkUpdates;
    
    public WardrobeProvider(EntityPlayer player) {
        allowNetworkUpdates = true;
        this.player = player;
        wardrobeInventoryContainer = new WardrobeInventoryContainer(this, validSkins);
        equipmentData = new EntityEquipmentData(1);
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability != null && capability == WARDROBE_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing)) {
            return WARDROBE_CAP.cast(this);
        }
        return null;
    }
    
    @Override
    public NBTBase serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        /*
        wardrobeInventoryContainer.writeToNBT(compound);
        equipmentWardrobeData.saveNBTData(compound);
        */
        compound.setInteger(TAG_LAST_XMAS_YEAR, this.lastXmasYear);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        /*
        wardrobeInventoryContainer.readFromNBT(compound);
        equipmentWardrobeData.loadNBTData(compound);
        allowNetworkUpdates = false;
        for (int i = 0; i < validSkins.length; i++) {
            WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(validSkins[i]);
            if (wi != null) {
                for (int j = 0; j < wi.getSizeInventory(); j++) {
                    armourSlotUpdate(wi, (byte)j);
                }
            }
        }
        allowNetworkUpdates = true;
        if (compound.hasKey(TAG_LAST_XMAS_YEAR)) {
            this.lastXmasYear = compound.getInteger(TAG_LAST_XMAS_YEAR);
        } else {
            this.lastXmasYear = 0;
        }
        */
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public WardrobeInventoryContainer getWardrobeInventoryContainer() {
        return wardrobeInventoryContainer;
    }

    @Override
    public IInventory getColumnInventory(ISkinType skinType) {
        return wardrobeInventoryContainer.getInventoryForSkinType(skinType);
    }

    @Override
    public void setSkinStack(ItemStack stack, int columnIndex) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer.skinType != null) {
            WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinPointer.skinType);
            if (wi != null) {
                wi.setInventorySlotContents(0, stack);
            }
        }
    }

    @Override
    public ItemStack getSkinStack(ISkinType skinType, int columnIndex) {
        WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinType);
        if (wi != null) {
            return wi.getStackInSlot(0);
        }
        return null;
    }

    @Override
    public void removeSkinStack(ISkinType skinType, int columnIndex) {
        WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinType);
        if (wi != null) {
            wi.setInventorySlotContents(columnIndex, null);
        }
    }

    @Override
    public void clearAllSkinStacks() {
        ArrayList<ISkinType> skinList = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        for (int i = 0; i < skinList.size() - 1; i++) {
            ISkinType skinType = skinList.get(i);
            WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinType);
            if (wi != null) {
                for (int j = 0; j < wi.getSizeInventory(); j++) {
                    wi.setInventorySlotContents(j, null);
                }
            }
        }
    }

    @Override
    public EquipmentWardrobeData getEquipmentWardrobeData() {
        return equipmentWardrobeData;
    }

    @Override
    public BitSet getArmourOverride() {
        return equipmentWardrobeData.armourOverride;
    }
    
    @Override
    public void sendWardrobeDataToAllAround() {
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 64);
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerSkinInfoUpdate(playerPointer, equipmentData), p);
    }
    
    @Override
    public void sendWardrobeDataToPlayer(EntityPlayerMP targetPlayer) {
        sendSkinDataToPlayer(targetPlayer);
        sendTextureDataToPlayer(targetPlayer);
    }
    
    private void sendSkinDataToPlayer(EntityPlayerMP targetPlayer) {
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendTo(new MessageServerSkinInfoUpdate(playerPointer, equipmentData), targetPlayer);
    }
    
    private void sendTextureDataToPlayer(EntityPlayerMP targetPlayer) {
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendTo(new MessageServerSkinWardrobeUpdate(playerPointer, this.equipmentWardrobeData), targetPlayer);
    }

    @Override
    public void setColumnCount(int count) {
        if (count > 0 & count < 6) {
            ModLogger.log("Setting slot count to " + count);
            equipmentWardrobeData.slotsUnlocked = count;
            sendTextureDataToPlayer((EntityPlayerMP) this.player);
        }
    }

    @Override
    public int getColumnCount() {
        return equipmentWardrobeData.slotsUnlocked;
    }

    @Override
    public void addColumn() {
        setColumnCount(getColumnCount() + 1);
    }

    @Override
    public void removeColumn() {
        setColumnCount(getColumnCount() - 1);
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int slotId, ItemStack stack) {
        if (!player.worldObj.isRemote) {
            if (inventory instanceof WardrobeInventory) {
                armourSlotUpdate((WardrobeInventory) inventory, (byte)slotId);
            }
        }
    }
    
    private void armourSlotUpdate(WardrobeInventory inventory, byte slot) {
        ItemStack stack = inventory.getStackInSlot(slot);
        
        if (stack == null) {
            removeArmourFromSlot(inventory, slot);
            return;
        } 
        
        if (!stack.hasTagCompound()) {
            return;
        }
        
        if (!SkinNBTHelper.stackHasSkinData(stack)) {
            return;
        }
        
        loadFromItemStack(stack, slot);
    }
    
    private void removeArmourFromSlot(WardrobeInventory inventory, byte slotId) {
        removeCustomEquipment(inventory.getSkinType(), slotId);
    }
    
    private void loadFromItemStack(ItemStack stack, byte slotId) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        addCustomEquipment(skinPointer.skinType, slotId, skinPointer);
    }
    
    private void addCustomEquipment(ISkinType skinType, byte slotId, SkinPointer skinPointer) {
        equipmentData.addEquipment(skinType, slotId, skinPointer);
        sendWardrobeDataToAllAround();
    }
    
    private void removeCustomEquipment(ISkinType skinType, byte slotId) {
        equipmentData.removeEquipment(skinType, slotId);
        sendWardrobeDataToAllAround();
    }
}
