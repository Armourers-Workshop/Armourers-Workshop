package riskyken.armourersWorkshop.common.customEquipment.armour;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.customEquipment.ExtendedPropsPlayerEquipmentData;

public class CustomArmourManager {
    
    public static void removeAllCustomArmourData(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData playerArmourData = ExtendedPropsPlayerEquipmentData.get(player);
        playerArmourData.removeAllCustomArmourData();
    }
}
