package riskyken.armourersWorkshop.common.skin;

import java.util.ArrayList;
import java.util.BitSet;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.items.ItemColourPicker;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerEquipmentWardrobeUpdate;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinInfoUpdate;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeHelper;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ExPropsPlayerEquipmentData implements IExtendedEntityProperties, IInventory {

    public static final String TAG_EXT_PROP_NAME = "playerCustomEquipmentData";
    private static final String TAG_ITEMS = "items";
    private static final String TAG_SLOT = "slot";
    private static final String TAG_LAST_XMAS_YEAR = "lastXmasYear";
    
    public ItemStack[] customArmourInventory = new ItemStack[9];
    private EntityEquipmentData equipmentData = new EntityEquipmentData();
    private final EntityPlayer player;
    private boolean inventoryChanged;
    private EquipmentWardrobeData equipmentWardrobeData = new EquipmentWardrobeData(); 
    public int lastXmasYear;
    
    public ExPropsPlayerEquipmentData(EntityPlayer player) {
        this.player = player;
    }
    
    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(ExPropsPlayerEquipmentData.TAG_EXT_PROP_NAME, new ExPropsPlayerEquipmentData(player));
    }
    
    public static final ExPropsPlayerEquipmentData get(EntityPlayer player) {
        return (ExPropsPlayerEquipmentData) player.getExtendedProperties(TAG_EXT_PROP_NAME);
    }
    
    public void setEquipmentStack(ItemStack stack) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer.skinType != null) {
            int slot = SkinTypeHelper.getSlotForSkinType(skinPointer.skinType);
            if (slot != -1) {
                setInventorySlotContents(slot, stack);
            }
        }
    }
    
    public ItemStack[] getAllEquipmentStacks() {
        return customArmourInventory;
    }
    
    public ItemStack getEquipmentStack(ISkinType skinType) {
        int slot = SkinTypeHelper.getSlotForSkinType(skinType);
        if (slot != -1) {
            return getStackInSlot(slot);
        }
        return null;
    }
    
    public void clearAllEquipmentStacks() {
        ArrayList<ISkinType> skinList = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        for (int i = 0; i < skinList.size() - 1; i++) {
            ISkinType skinType = skinList.get(i);
            int slot = SkinTypeHelper.getSlotForSkinType(skinType);
            if (slot != -1) {
                setInventorySlotContents(slot, null);
            }
        }
    }
    
    public void clearEquipmentStack(ISkinType skinType) {
        int slot = SkinTypeHelper.getSlotForSkinType(skinType);
        if (slot != -1) {
            setInventorySlotContents(slot, null);
        }
    }
    
    public void addCustomEquipment(ISkinType skinType, SkinPointer skinPointer) {
        equipmentData.addEquipment(skinType, skinPointer);
        updateEquipmentDataToPlayersAround();
    }
    
    public void removeCustomEquipment(ISkinType skinType) {
        equipmentData.removeEquipment(skinType);
        updateEquipmentDataToPlayersAround();
    }
    
    private void updateEquipmentDataToPlayersAround() {
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerSkinInfoUpdate(playerPointer, equipmentData), p);
    }
    
    public void colourSlotUpdate(byte slot) {
        ItemStack stackInput = this.getStackInSlot(slot);
        ItemStack stackOutput = this.getStackInSlot(slot + 1);
        
        if (stackInput != null && stackInput.getItem() == ModItems.colourPicker && stackOutput == null) {
            this.equipmentWardrobeData.skinColour = ((ItemColourPicker)stackInput.getItem()).getToolColour(stackInput);
            
            setInventorySlotContents(slot + 1, stackInput);
            setInventorySlotContents(slot, null);
            sendSkinData();
        }
    }
    
    public EntityEquipmentData getEquipmentData() {
        return equipmentData;
    }
    
    public void armourSlotUpdate(byte slot) {
        ItemStack stack = this.getStackInSlot(slot);
        
        if (stack == null) {
            removeArmourFromSlot(slot);
            return;
        } 
        
        if (!stack.hasTagCompound()) {
            return;
        }
        
        if (!SkinNBTHelper.stackHasSkinData(stack)) {
            return;
        }
        
        loadFromItemStack(stack);
    }
    
    private void removeArmourFromSlot(byte slotId) {
        ISkinType skinType = SkinTypeHelper.getSkinTypeForSlot(slotId);
        if (slotId != -1) {
            removeCustomEquipment(skinType);
        }
    }
    
    public void sendCustomArmourDataToPlayer(EntityPlayerMP targetPlayer) {
        checkAndSendCustomArmourDataTo(targetPlayer);
        sendNakedData(targetPlayer);
    }
    
    public void setSkinInfo(EquipmentWardrobeData equipmentWardrobeData) {
        this.equipmentWardrobeData = equipmentWardrobeData;
        sendSkinData();
    }
    
    private void checkAndSendCustomArmourDataTo(EntityPlayerMP targetPlayer) {
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendTo(new MessageServerSkinInfoUpdate(playerPointer, equipmentData), targetPlayer);
    }
    
    private void sendNakedData(EntityPlayerMP targetPlayer) {
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendTo(new MessageServerEquipmentWardrobeUpdate(playerPointer, this.equipmentWardrobeData), targetPlayer);
    }
    
    private void sendSkinData() {
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PlayerPointer playerPointer = new PlayerPointer(player);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerEquipmentWardrobeUpdate(playerPointer, this.equipmentWardrobeData), p);
    }
    
    public BitSet getArmourOverride() {
        return equipmentWardrobeData.armourOverride;
    }
    
    @Override
    public void saveNBTData(NBTTagCompound compound) {
        writeItemsToNBT(compound);
        equipmentWardrobeData.saveNBTData(compound);
        compound.setInteger(TAG_LAST_XMAS_YEAR, this.lastXmasYear);
    }
    
    @Override
    public void loadNBTData(NBTTagCompound compound) {
        readItemsFromNBT(compound);
        equipmentWardrobeData.loadNBTData(compound);
        if (compound.hasKey(TAG_LAST_XMAS_YEAR)) {
            this.lastXmasYear = compound.getInteger(TAG_LAST_XMAS_YEAR);
        } else {
            this.lastXmasYear = 0;
        }
    }
    
    private void loadFromItemStack(ItemStack stack) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        addCustomEquipment(skinPointer.skinType, skinPointer);
    }
    
    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public int getSizeInventory() {
        return customArmourInventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return customArmourInventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack itemstack = getStackInSlot(slot);
        
        if (itemstack != null) {
            if (itemstack.stackSize <= count){
                setInventorySlotContents(slot, null);
            }else{
                itemstack = itemstack.splitStack(count);
                markDirty();
            }
        }
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack item = getStackInSlot(slot);
        setInventorySlotContents(slot, null);
        return item;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        customArmourInventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        if (!player.worldObj.isRemote) {
            if (slot < 7) {
                armourSlotUpdate((byte)slot);
            }
        }

        colourSlotUpdate((byte)7);
        
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "pasta";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        this.inventoryChanged = true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
    
    public void writeItemsToNBT(NBTTagCompound compound) {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte(TAG_SLOT, (byte)i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag(TAG_ITEMS, items);
    }
    
    public void readItemsFromNBT(NBTTagCompound compound) {
        NBTTagList items = compound.getTagList(TAG_ITEMS, NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound item = (NBTTagCompound)items.getCompoundTagAt(i);
            int slot = item.getByte(TAG_SLOT);
            
            if (slot >= 0 && slot < getSizeInventory()) {
                setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
            }
        }
    }
    
    public void dropItems() {
        World world = player.worldObj;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack != null) {
                float f = 0.7F;
                double xV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double yV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double zV = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world, (double)x + xV, (double)y + yV, (double)z + zV, stack);
                world.spawnEntityInWorld(entityitem);
                setInventorySlotContents(i, null);
            }
        }
    }
}
