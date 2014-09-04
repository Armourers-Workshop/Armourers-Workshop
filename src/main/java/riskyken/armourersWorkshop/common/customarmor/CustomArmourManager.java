package riskyken.armourersWorkshop.common.customarmor;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;

public class CustomArmourManager {
    
    public static void addCustomArmour(EntityPlayer player, CustomArmourItemData armourData) {
        PlayerCustomArmourData playerArmourData = PlayerCustomArmourData.get(player);
        playerArmourData.addCustomArmour(armourData);
    }

    public static void removeCustomArmour(EntityPlayer player, ArmourerType type) {
        PlayerCustomArmourData playerArmourData = PlayerCustomArmourData.get(player);
        playerArmourData.removeCustomArmour(type);
    }

    public static void removeAllCustomArmourData(EntityPlayer player) {
        PlayerCustomArmourData playerArmourData = PlayerCustomArmourData.get(player);
        playerArmourData.removeAllCustomArmourData();
    }

    public static void playerArmourSlotUpdate(EntityPlayer player,byte slotId, boolean added) {
        PlayerCustomArmourData playerArmourData = PlayerCustomArmourData.get(player);
        playerArmourData.armourSlotUpdate(slotId, added);
    }
}
