package riskyken.armourersWorkshop.common.custom.equipment.armour;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.custom.equipment.ExtendedPropsPlayerEquipmentData;

public class CustomArmourManager {
    /*
    public static void addCustomArmour(EntityPlayer player, CustomArmourItemData armourData) {
        PlayerCustomEquipmentData playerArmourData = PlayerCustomEquipmentData.get(player);
        playerArmourData.addCustomArmour(armourData);
    }

    public static void removeCustomArmour(EntityPlayer player, ArmourType type) {
        PlayerCustomEquipmentData playerArmourData = PlayerCustomEquipmentData.get(player);
        playerArmourData.removeCustomArmour(type);
    }
*/
    
    public static void removeAllCustomArmourData(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData playerArmourData = ExtendedPropsPlayerEquipmentData.get(player);
        playerArmourData.removeAllCustomArmourData();
    }
    
}
