package riskyken.armourersWorkshop.common.customarmor;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
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
    }

    public static void removeAllCustomArmourData(Entity entity) {
        if (!(entity instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) entity;
        ModLogger.log("Removing custom armour for " + player.getDisplayName());
        removeCustomArmour(player, ArmourerType.HEAD, ArmourPart.HEAD);
        removeCustomArmour(player, ArmourerType.CHEST, ArmourPart.CHEST);
        removeCustomArmour(player, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        removeCustomArmour(player, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        removeCustomArmour(player, ArmourerType.LEGS, ArmourPart.LEFT_LEG);
        removeCustomArmour(player, ArmourerType.LEGS, ArmourPart.RIGHT_LEG);
        removeCustomArmour(player, ArmourerType.LEGS, ArmourPart.SKIRT);
    }
}
