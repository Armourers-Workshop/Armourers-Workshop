package riskyken.armourersWorkshop.common.custom.equipment;

import java.awt.Color;
import java.util.BitSet;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ItemColourPicker;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerRemoveArmourData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerUpdateSkinInfo;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class PlayerCustomEquipmentData implements IExtendedEntityProperties, IInventory {

    public final static String TAG_EXT_PROP_NAME = "playerCustomEquipmentData";
    private static final String TAG_ITEMS = "items";
    private static final String TAG_SLOT = "slot";
    private static final String TAG_NAKED = "naked";
    private static final String TAG_SKIN_COLOUR = "skinColour";
    private static final String TAG_PANTS_COLOUR = "pantsColour";
    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    private static final String TAG_HEAD_OVERLAY = "headOverlay";
    
    public ItemStack[] customArmourInventory = new ItemStack[8];
    private final EntityPlayer player;
    private final HashMap<String, CustomArmourItemData> customArmor;
    private boolean inventoryChanged;
    
    private boolean isNaked = false;
    private int skinColour = Color.decode("#F9DFD2").getRGB();
    private int pantsColour = Color.decode("#FCFCFC").getRGB();
    BitSet armourOverride = new BitSet(4);
    boolean headOverlay;
    
    public PlayerCustomEquipmentData(EntityPlayer player) {
        this.player = player;
        customArmor = new HashMap<String, CustomArmourItemData>();
    }
    
    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(PlayerCustomEquipmentData.TAG_EXT_PROP_NAME, new PlayerCustomEquipmentData(player));
    }
    
    public static final PlayerCustomEquipmentData get(EntityPlayer player) {
        return (PlayerCustomEquipmentData) player.getExtendedProperties(TAG_EXT_PROP_NAME);
    }
    
    public void addCustomArmour(CustomArmourItemData armourData) {
        String key = armourData.getType().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        customArmor.put(key, armourData);
        
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerAddArmourData(player.getPersistentID(), armourData), p);
    }

    public void removeCustomArmour(ArmourType type) {
        String key = type.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerRemoveArmourData(player.getPersistentID(), type), p);
    }
    
    public void colourSlotUpdate(byte slot) {
        ItemStack stackInput = this.getStackInSlot(slot);
        ItemStack stackOutput = this.getStackInSlot(slot + 1);
        
        if (stackInput != null && stackInput.getItem() == ModItems.colourPicker && stackOutput == null) {
            //Silliness!
            if (stackInput.getDisplayName().toLowerCase().equals("panties!")) {
                this.pantsColour = ((ItemColourPicker)stackInput.getItem()).getToolColour(stackInput);
            } else {
                this.skinColour = ((ItemColourPicker)stackInput.getItem()).getToolColour(stackInput);
            }
            
            setInventorySlotContents(slot + 1, stackInput);
            setInventorySlotContents(slot, null);
            sendSkinData();
        }
    }

    public void armourSlotUpdate(byte slot) {
        ItemStack stack = this.getStackInSlot(slot);
        
        if (stack == null) {
            removeArmourFromSlot(slot);
            return;
        } 
        
        if (!stack.hasTagCompound()) { return; }
        
        NBTTagCompound data = stack.getTagCompound();
        if (!data.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return ;}
        NBTTagCompound armourNBT = data.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        loadFromItemNBT(armourNBT);
    }
    
    private void removeArmourFromSlot(byte slotId) {
        switch (slotId) {
        case 4:
            removeCustomArmour(ArmourType.FEET);
            break;
        case 3:
            removeCustomArmour(ArmourType.SKIRT);
            break;  
        case 2:
            removeCustomArmour(ArmourType.LEGS);
            break;
        case 1:
            removeCustomArmour(ArmourType.CHEST);
            break;
        case 0:
            removeCustomArmour(ArmourType.HEAD);
            break;
        }
    }
    
    public void removeAllCustomArmourData() {
        player.addChatMessage(new ChatComponentText("You're custom armour data was cleared."));
        removeCustomArmour(ArmourType.HEAD);
        removeCustomArmour(ArmourType.CHEST);
        removeCustomArmour(ArmourType.LEGS);
        removeCustomArmour(ArmourType.SKIRT);
        removeCustomArmour(ArmourType.FEET);
    }
    
    public void sendCustomArmourDataToPlayer(EntityPlayerMP targetPlayer) {
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourType.HEAD);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourType.CHEST);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourType.LEGS);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourType.SKIRT);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourType.FEET);
        sendNakedData(targetPlayer);
    }
    
    public void setSkinInfo(boolean naked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        this.isNaked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
        this.armourOverride = armourOverride;
        this.headOverlay = headOverlay;
        sendSkinData();
    }
    
    private void checkAndSendCustomArmourDataTo(EntityPlayerMP targetPlayer, ArmourType type) {
        String key = type.name();
        if (customArmor.containsKey(key)) {
            CustomArmourItemData data = customArmor.get(key);
            PacketHandler.networkWrapper.sendTo(new MessageServerAddArmourData(player.getPersistentID(), data), targetPlayer);
        } else {
            PacketHandler.networkWrapper.sendTo(new MessageServerRemoveArmourData(player.getPersistentID(), type), targetPlayer);
        }
    }
    
    private void sendNakedData(EntityPlayerMP targetPlayer) {
        PacketHandler.networkWrapper.sendTo(new MessageServerUpdateSkinInfo(this.player.getPersistentID(), this.isNaked, this.skinColour, this.pantsColour, armourOverride, headOverlay), targetPlayer);
    }
    
    private void sendSkinData() {
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerUpdateSkinInfo(this.player.getPersistentID(), this.isNaked, this.skinColour, this.pantsColour, armourOverride, headOverlay), p);
    }
    
    @Override
    public void saveNBTData(NBTTagCompound compound) {
        writeItemsToNBT(compound);
        compound.setBoolean(TAG_NAKED, this.isNaked);
        compound.setInteger(TAG_SKIN_COLOUR, this.skinColour);
        compound.setInteger(TAG_PANTS_COLOUR, this.pantsColour);
        for (int i = 0; i < 4; i++) {
        	compound.setBoolean(TAG_ARMOUR_OVERRIDE + i, this.armourOverride.get(i));
        }
        compound.setBoolean(TAG_HEAD_OVERLAY, this.headOverlay);
        //TODO Change to for loop
        //TODO Maybe save a list?
        saveKey(compound, ArmourType.HEAD);
        saveKey(compound, ArmourType.CHEST);
        saveKey(compound, ArmourType.LEGS);
        saveKey(compound, ArmourType.SKIRT);
        saveKey(compound, ArmourType.FEET);
    }
    
    private void saveKey(NBTTagCompound compound, ArmourType type) {
        String key = type.name();
        if (customArmor.containsKey(key)) {
            NBTTagCompound dataNBT = new NBTTagCompound();
            customArmor.get(key).writeToNBT(dataNBT);
            compound.setTag(key, dataNBT);
        }
    }
    
    @Override
    public void loadNBTData(NBTTagCompound compound) {
        readItemsFromNBT(compound);
        this.isNaked = compound.getBoolean(TAG_NAKED);
        if (compound.hasKey(TAG_SKIN_COLOUR)) {
            this.skinColour = compound.getInteger(TAG_SKIN_COLOUR);
        }
        if (compound.hasKey(TAG_PANTS_COLOUR)) {
            this.pantsColour = compound.getInteger(TAG_PANTS_COLOUR);
        }
        for (int i = 0; i < 4; i++) {
        	this.armourOverride.set(i, compound.getBoolean(TAG_ARMOUR_OVERRIDE + i));
        }
        this.headOverlay = compound.getBoolean(TAG_HEAD_OVERLAY);
        
        loadKey(compound, ArmourType.HEAD);
        loadKey(compound, ArmourType.CHEST);
        loadKey(compound, ArmourType.LEGS);
        loadKey(compound, ArmourType.SKIRT);
        loadKey(compound, ArmourType.FEET);
    }
    
    private void loadKey(NBTTagCompound compound, ArmourType type) {
        String key = type.name();
        if (compound.hasKey(key)) {
            
            NBTTagCompound dataNBT = compound.getCompoundTag(key);
            customArmor.put(key, new CustomArmourItemData(dataNBT));
        }
    }
    
    private void loadFromItemNBT(NBTTagCompound compound) {
        int equipmentId = compound.getInteger(LibCommonTags.TAG_EQUPMENT_ID);
        CustomArmourItemData equipmentData = EquipmentDataCache.getEquipmentData(equipmentId);
        
        addCustomArmour(equipmentData);
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
            if (slot < 5) {
                armourSlotUpdate((byte)slot);
            }

        }

        colourSlotUpdate((byte)6);
        
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
            }
        }
    }
}
