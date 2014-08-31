package riskyken.armourersWorkshop.common.customarmor;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerRemoveArmourData;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class PlayerCustomArmourData implements IExtendedEntityProperties {

    public final static String TAG_EXT_PROP_NAME = "playerCustomArmourData";
    
    private final EntityPlayer player;
    private final HashMap<String, CustomArmourData> customArmor;
    
    public PlayerCustomArmourData(EntityPlayer player) {
        this.player = player;
        customArmor = new HashMap<String, CustomArmourData>();
    }
    
    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(PlayerCustomArmourData.TAG_EXT_PROP_NAME, new PlayerCustomArmourData(player));
    }
    
    public static final PlayerCustomArmourData get(EntityPlayer player) {
        return (PlayerCustomArmourData) player.getExtendedProperties(TAG_EXT_PROP_NAME);
    }
    
    public void addCustomArmour(CustomArmourData armourData) {
        String key = armourData.getArmourType().name() + ":" + armourData.getArmourPart().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        customArmor.put(key, armourData);
        
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerAddArmourData(player.getDisplayName(), armourData), p);
    }
    
    public void removeCustomArmour(ArmourerType type, ArmourPart part) {
        String key = type.name() + ":" + part.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        
        TargetPoint p = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerRemoveArmourData(player.getDisplayName(), type, part), p);
    }
    
    public void removeAllCustomArmourData() {
        player.addChatMessage(new ChatComponentText("You're custom armour data was cleared."));
        removeCustomArmour(ArmourerType.HEAD, ArmourPart.HEAD);
        removeCustomArmour(ArmourerType.CHEST, ArmourPart.CHEST);
        removeCustomArmour(ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        removeCustomArmour(ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        removeCustomArmour(ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        removeCustomArmour(ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        removeCustomArmour(ArmourerType.LEGS, ArmourPart.SKIRT);
    }
    
    public void sendCustomArmourDataToPlayer(EntityPlayerMP targetPlayer) {
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.HEAD, ArmourPart.HEAD);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.CHEST, ArmourPart.CHEST);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        checkAndSendCustomArmourDataTo(targetPlayer, ArmourerType.LEGS, ArmourPart.SKIRT);
    }
    
    private void checkAndSendCustomArmourDataTo(EntityPlayerMP targetPlayer, ArmourerType type, ArmourPart part) {
        String key = type.name() + ":" + part.name();
        if (customArmor.containsKey(key)) {
            CustomArmourData data = customArmor.get(key);
            PacketHandler.networkWrapper.sendTo(new MessageServerAddArmourData(player.getDisplayName(), data), targetPlayer);
        } else {
            PacketHandler.networkWrapper.sendTo(new MessageServerRemoveArmourData(player.getDisplayName(), type, part), targetPlayer);
        }
    }
    
    @Override
    public void saveNBTData(NBTTagCompound compound) {
        saveKey(compound, ArmourerType.HEAD, ArmourPart.HEAD);
        saveKey(compound, ArmourerType.CHEST, ArmourPart.CHEST);
        saveKey(compound, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        saveKey(compound, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        saveKey(compound, ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        saveKey(compound, ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        saveKey(compound, ArmourerType.LEGS, ArmourPart.SKIRT);
    }
    
    private void saveKey(NBTTagCompound compound, ArmourerType type, ArmourPart part) {
        String key = type.name() + ":" + part.name();
        if (customArmor.containsKey(key)) {
            NBTTagCompound dataNBT = new NBTTagCompound();
            customArmor.get(key).writeToNBT(dataNBT);
            compound.setTag(key, dataNBT);
        }
    }
    
    @Override
    public void loadNBTData(NBTTagCompound compound) {
        loadKey(compound, ArmourerType.HEAD, ArmourPart.HEAD);
        loadKey(compound, ArmourerType.CHEST, ArmourPart.CHEST);
        loadKey(compound, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        loadKey(compound, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        loadKey(compound, ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        loadKey(compound, ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        loadKey(compound, ArmourerType.LEGS, ArmourPart.SKIRT);
    }
    
    private void loadKey(NBTTagCompound compound, ArmourerType type, ArmourPart part) {
        String key = type.name() + ":" + part.name();
        if (compound.hasKey(key)) {
            
            NBTTagCompound dataNBT = compound.getCompoundTag(key);
            customArmor.put(key, new CustomArmourData(dataNBT));
        }
    }

    @Override
    public void init(Entity entity, World world) {
        
    }
}
