package riskyken.armourersWorkshop.common.customarmor;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerRemoveArmourData;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class PlayerCustomArmourData implements IExtendedEntityProperties {

    public final static String TAG_EXT_PROP_NAME = "playerCustomArmourData";
    private static final String TAG_ARMOUR_DATA = "armourData";
    
    private final EntityPlayer player;
    private final HashMap<String, CustomArmourItemData> customArmor;
    
    public PlayerCustomArmourData(EntityPlayer player) {
        this.player = player;
        customArmor = new HashMap<String, CustomArmourItemData>();
    }
    
    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(PlayerCustomArmourData.TAG_EXT_PROP_NAME, new PlayerCustomArmourData(player));
    }
    
    public static final PlayerCustomArmourData get(EntityPlayer player) {
        return (PlayerCustomArmourData) player.getExtendedProperties(TAG_EXT_PROP_NAME);
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
    
    public void removeCustomArmour(ArmourerType type) {
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
            removeCustomArmour(ArmourerType.FEET);
            break;
        case 1:
            removeCustomArmour(ArmourerType.LEGS);
            removeCustomArmour(ArmourerType.SKIRT);
            break;
        case 2:
            removeCustomArmour(ArmourerType.CHEST);
            break;
        case 3:
            removeCustomArmour(ArmourerType.HEAD);
            break;
        }
    }
    
    public void removeAllCustomArmourData() {
        player.addChatMessage(new ChatComponentText("You're custom armour data was cleared."));
        removeCustomArmour(ArmourerType.HEAD);
        removeCustomArmour(ArmourerType.CHEST);
        removeCustomArmour(ArmourerType.LEGS);
        removeCustomArmour(ArmourerType.SKIRT);
        removeCustomArmour(ArmourerType.FEET);
    }
    
    public void sendCustomArmourDataToPlayer(EntityPlayerMP targetPlayer) {
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.HEAD);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.CHEST);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.LEGS);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.SKIRT);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.FEET);
    }
    
    private void checkAndSendCustomArmourDataTo(EntityPlayerMP targetPlayer, ArmourerType type) {
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
        saveKey(compound, ArmourerType.HEAD);
        saveKey(compound, ArmourerType.CHEST);
        saveKey(compound, ArmourerType.LEGS);
        saveKey(compound, ArmourerType.SKIRT);
        saveKey(compound, ArmourerType.FEET);
    }
    
    private void saveKey(NBTTagCompound compound, ArmourerType type) {
        String key = type.name();
        if (customArmor.containsKey(key)) {
            NBTTagCompound dataNBT = new NBTTagCompound();
            customArmor.get(key).writeToNBT(dataNBT);
            compound.setTag(key, dataNBT);
        }
    }
    
    @Override
    public void loadNBTData(NBTTagCompound compound) {
        loadKey(compound, ArmourerType.HEAD);
        loadKey(compound, ArmourerType.CHEST);
        loadKey(compound, ArmourerType.LEGS);
        loadKey(compound, ArmourerType.SKIRT);
        loadKey(compound, ArmourerType.FEET);
    }
    
    private void loadKey(NBTTagCompound compound, ArmourerType type) {
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
