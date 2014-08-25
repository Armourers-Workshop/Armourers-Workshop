package riskyken.armourersWorkshop.common.customarmor;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerRemoveArmourData;
import riskyken.armourersWorkshop.utils.ModLogger;

public class CustomArmourManager {

    public static HashMap<String, CustomArmourData> customArmor = new HashMap<String, CustomArmourData>();
    
    public static void init() {
        ModLogger.log("CustomArmourManager init");
    }
    
    public static void addCustomArmour(Entity entity, CustomArmourData armourData) {
        if (!(entity instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) entity;
        
        String key = player.getDisplayName() + ":" + armourData.getArmourType().name() + ":" + armourData.getArmourPart().name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        customArmor.put(key, armourData);
        PacketHandler.networkWrapper.sendToAll(new MessageServerAddArmourData(player.getDisplayName(), armourData));
    }

    public static void removeCustomArmour(Entity entity, ArmourerType type, ArmourPart part) {
        if (!(entity instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) entity;
        String key = player.getDisplayName() + ":" + type.name() + ":" + part.name();
        if (customArmor.containsKey(key)) {
            customArmor.remove(key);
        }
        PacketHandler.networkWrapper.sendToAll(new MessageServerRemoveArmourData(player.getDisplayName(), type, part));
    }

    public static void removeAllCustomArmourData(Entity entity) {
        if (!(entity instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) entity;
        player.addChatMessage(new ChatComponentText("You're custom armour data was cleared."));
        removeCustomArmour(player, ArmourerType.HEAD, ArmourPart.HEAD);
        removeCustomArmour(player, ArmourerType.CHEST, ArmourPart.CHEST);
        removeCustomArmour(player, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        removeCustomArmour(player, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        removeCustomArmour(player, ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        removeCustomArmour(player, ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        removeCustomArmour(player, ArmourerType.LEGS, ArmourPart.SKIRT);
        ModLogger.log("Removing custom armour for " + player.getDisplayName());
    }
}
