package riskyken.armourersWorkshop.common.equipment.armour;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;

public class CustomArmourManager {
    
    public static void removeAllCustomArmourData(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData playerArmourData = ExtendedPropsPlayerEquipmentData.get(player);
        playerArmourData.removeAllCustomArmourData();
    }
}
