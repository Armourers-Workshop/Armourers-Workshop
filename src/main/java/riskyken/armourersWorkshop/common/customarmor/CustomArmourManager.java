package riskyken.armourersWorkshop.common.customarmor;

import net.minecraft.entity.player.EntityPlayer;

public class CustomArmourManager {
    
    public static void addCustomArmour(EntityPlayer player, CustomArmourData armourData) {
        PlayerCustomArmourData playerArmourData = PlayerCustomArmourData.get(player);
        playerArmourData.addCustomArmour(armourData);
    }

    public static void removeCustomArmour(EntityPlayer player, ArmourerType type, ArmourPart part) {
        PlayerCustomArmourData playerArmourData = PlayerCustomArmourData.get(player);
        playerArmourData.removeCustomArmour(type, part);
    }

    public static void removeAllCustomArmourData(EntityPlayer player) {
        PlayerCustomArmourData playerArmourData = PlayerCustomArmourData.get(player);
        playerArmourData.removeAllCustomArmourData();
    }
}
