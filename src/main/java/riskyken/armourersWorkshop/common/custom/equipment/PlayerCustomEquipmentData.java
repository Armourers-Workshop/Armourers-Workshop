package riskyken.armourersWorkshop.common.custom.equipment;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerRemoveArmourData;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class PlayerCustomEquipmentData implements IExtendedEntityProperties {

    public final static String TAG_EXT_PROP_NAME = "playerCustomEquipmentData";
    private static final String TAG_ARMOUR_DATA = "armourData";
    //private static final String TAG_WEAPON_DATA = "weaponData";
    
    private final EntityPlayer player;
    private final HashMap<String, CustomArmourItemData> customArmor;
    
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
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerAddArmourData(player.getDisplayName(), armourData), p);
    }
    
    public void removeCustomArmour(ArmourType type) {
        String key = type.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerRemoveArmourData(player.getDisplayName(), type), p);
    }
    

    public void armourSlotUpdate(byte slotId, boolean added) {
        ItemStack stack = player.getCurrentArmor(slotId);
        
        if (!added)
        {
            removeArmourFromSlot(slotId);
            return;
        }
        if (stack == null) { return; }
        
        if (!stack.hasTagCompound()) { return; }
        
        NBTTagCompound data = stack.getTagCompound();
        if (!data.hasKey(TAG_ARMOUR_DATA)) { return ;}
        NBTTagCompound armourNBT = data.getCompoundTag(TAG_ARMOUR_DATA);
        loadFromItemNBT(armourNBT);
        ModLogger.log(stack);
    }
    
    private void removeArmourFromSlot(byte slotId) {
        switch (slotId) {
        case 0:
            removeCustomArmour(ArmourType.FEET);
            break;
        case 1:
            removeCustomArmour(ArmourType.LEGS);
            removeCustomArmour(ArmourType.SKIRT);
            break;
        case 2:
            removeCustomArmour(ArmourType.CHEST);
            break;
        case 3:
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
    }
    
    private void checkAndSendCustomArmourDataTo(EntityPlayerMP targetPlayer, ArmourType type) {
        String key = type.name();
        if (customArmor.containsKey(key)) {
            CustomArmourItemData data = customArmor.get(key);
            PacketHandler.networkWrapper.sendTo(new MessageServerAddArmourData(player.getDisplayName(), data), targetPlayer);
        } else {
            PacketHandler.networkWrapper.sendTo(new MessageServerRemoveArmourData(player.getDisplayName(), type), targetPlayer);
        }
    }
    
    @Override
    public void saveNBTData(NBTTagCompound compound) {
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
        addCustomArmour(new CustomArmourItemData(compound));
    }
    
    @Override
    public void init(Entity entity, World world) {
        
    }
}
