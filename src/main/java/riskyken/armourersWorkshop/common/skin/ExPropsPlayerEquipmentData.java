package riskyken.armourersWorkshop.common.skin;

import java.util.ArrayList;
import java.util.BitSet;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.inventory.IInventorySlotUpdate;
import riskyken.armourersWorkshop.common.inventory.WardrobeInventory;
import riskyken.armourersWorkshop.common.inventory.WardrobeInventoryContainer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinInfoUpdate;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinWardrobeUpdate;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ExPropsPlayerEquipmentData implements IExtendedEntityProperties, IInventorySlotUpdate {

    public static final int MAX_SLOTS_PER_SKIN_TYPE = 8;
    public static final String TAG_EXT_PROP_NAME = "playerCustomEquipmentData";
    private static final String TAG_LAST_XMAS_YEAR = "lastXmasYear";
    public static final ISkinType[] validSkins = {
            SkinTypeRegistry.skinHead,
            SkinTypeRegistry.skinChest,
            SkinTypeRegistry.skinLegs,
            SkinTypeRegistry.skinFeet,
            SkinTypeRegistry.skinSword,
            SkinTypeRegistry.skinBow,
            SkinTypeRegistry.skinArrow,
            SkinTypeRegistry.skinWings
            };
    
    private final WardrobeInventoryContainer wardrobeInventoryContainer;
    private final EntityEquipmentData equipmentData;
    private final EntityPlayer player;
    private EquipmentWardrobeData equipmentWardrobeData = new EquipmentWardrobeData(); 
    public int lastXmasYear;
    private boolean allowNetworkUpdates;
    
    public ExPropsPlayerEquipmentData(EntityPlayer player) {
        allowNetworkUpdates = true;
        this.player = player;
        //An array of all the skins that can be placed in the players wardrobe.

        wardrobeInventoryContainer = new WardrobeInventoryContainer(this, validSkins);
        equipmentData = new EntityEquipmentData(1);
    }
    
    public WardrobeInventoryContainer getWardrobeInventoryContainer() {
        return wardrobeInventoryContainer;
    }
    
    public EntityPlayer getPlayer() {
        return player;
    }
    
    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(ExPropsPlayerEquipmentData.TAG_EXT_PROP_NAME, new ExPropsPlayerEquipmentData(player));
    }
    
    public static final ExPropsPlayerEquipmentData get(EntityPlayer player) {
        return (ExPropsPlayerEquipmentData) player.getExtendedProperties(TAG_EXT_PROP_NAME);
    }
    
    /**
     * @Deprecated Use the {@link #setEquipmentStack(ItemStack, int) setEquipmentStack(ItemStack stack, int index)} method.
     */
    @Deprecated
    public void setEquipmentStack(ItemStack stack) {
        setEquipmentStack(stack, 0);
    }
    
    public void setEquipmentStack(ItemStack stack, int index) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer.skinType != null) {
            WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinPointer.skinType);
            if (wi != null) {
                wi.setInventorySlotContents(index, stack);
            }
        }
    }
    /** See also {@link #getEquipmentStack(String)}. */
    /**
     * @Deprecated Use the {@link #getEquipmentStack(ItemStack, int) getEquipmentStack(ItemStack stack, int index)} method.
     */
    @Deprecated
    public ItemStack getEquipmentStack(ISkinType skinType) {
        return getEquipmentStack(skinType, 0);
    }
    
    public ItemStack getEquipmentStack(ISkinType skinType, int index) {
        WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinType);
        if (wi != null) {
            return wi.getStackInSlot(index);
        }
        return null;
    }
    
    public void clearAllEquipmentStacks() {
        ArrayList<ISkinType> skinList = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinType);
            if (wi != null) {
                for (int j = 0; j < wi.getSizeInventory(); j++) {
                    wi.setInventorySlotContents(j, null);
                }
            }
        }
    }
    
    @Deprecated
    public void clearEquipmentStack(ISkinType skinType) {
        clearEquipmentStack(skinType, 0);
    }
    
    public void clearEquipmentStack(ISkinType skinType, int index) {
        WardrobeInventory wi = wardrobeInventoryContainer.getInventoryForSkinType(skinType);
        if (wi != null) {
            wi.setInventorySlotContents(index, null);
        }
    }
    
    public void addCustomEquipment(ISkinType skinType, byte slotId, SkinPointer skinPointer) {
        equipmentData.addEquipment(skinType, slotId, skinPointer);
        updateEquipmentDataToPlayersAround();
    }
    
    public void removeCustomEquipment(ISkinType skinType, byte slotId) {
        equipmentData.removeEquipment(skinType, slotId);
        updateEquipmentDataToPlayersAround();
    }
    
    public void updateEquipmentDataToPlayersAround() {
        if (!allowNetworkUpdates) {
            return;
        }
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerSkinInfoUpdate(playerPointer, equipmentData), p);
    }
    
    public EntityEquipmentData getEquipmentData() {
        return equipmentData;
    }
    
    public void armourSlotUpdate(WardrobeInventory inventory, byte slot) {
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
    
    public void setSkinColumnCount(ISkinType skinType, int value) {
        if (value > 0 & value <= MAX_SLOTS_PER_SKIN_TYPE) {
            ModLogger.log(String.format("Setting slot count for %s to %d.", skinType.getRegistryName() ,value));
            equipmentWardrobeData.setUnlockedSlotsForSkinType(skinType, value);
            sendNakedData((EntityPlayerMP) this.player);
        }
    }
    
    public void sendCustomArmourDataToPlayer(EntityPlayerMP targetPlayer) {
        checkAndSendCustomArmourDataTo(targetPlayer);
        sendNakedData(targetPlayer);
    }
    
    public void setSkinInfo(EquipmentWardrobeData equipmentWardrobeData, boolean sendUpdate) {
        this.equipmentWardrobeData = equipmentWardrobeData;
        if (sendUpdate) {
            sendSkinData();
        }
    }
    
    private void checkAndSendCustomArmourDataTo(EntityPlayerMP targetPlayer) {
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendTo(new MessageServerSkinInfoUpdate(playerPointer, equipmentData), targetPlayer);
    }
    
    private void sendNakedData(EntityPlayerMP targetPlayer) {
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendTo(new MessageServerSkinWardrobeUpdate(playerPointer, this.equipmentWardrobeData), targetPlayer);
    }
    
    private void sendSkinData() {
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerSkinWardrobeUpdate(playerPointer, this.equipmentWardrobeData), p);
    }
    
    public EquipmentWardrobeData getEquipmentWardrobeData() {
        return equipmentWardrobeData;
    }
    
    public BitSet getArmourOverride() {
        return equipmentWardrobeData.armourOverride;
    }
    
    @Override
    public void saveNBTData(NBTTagCompound compound) {
        wardrobeInventoryContainer.writeToNBT(compound);
        equipmentWardrobeData.saveNBTData(compound);
        compound.setInteger(TAG_LAST_XMAS_YEAR, this.lastXmasYear);
    }
    
    @Override
    public void loadNBTData(NBTTagCompound compound) {
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
    }
    
    private void loadFromItemStack(ItemStack stack, byte slotId) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        addCustomEquipment(skinPointer.skinType, slotId, skinPointer);
    }
    
    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int slotId, ItemStack stack) {
        if (!player.worldObj.isRemote) {
            if (inventory instanceof WardrobeInventory) {
                armourSlotUpdate((WardrobeInventory) inventory, (byte)slotId);
            }
        }
    }
}
